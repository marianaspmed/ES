package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(Question question, MultipleChoiceQuestionDto questionDto) {
        super(question);
        setOptions(questionDto.getOptions());
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        // verify question has at least one correct option

        List<Integer> everyRelevancy = new ArrayList<>();
        int numCorrectOptions = 0;
        int index = 0;

        // cycle through saved options
        for (Option op : getOptions()) {
            if (!op.isCorrect()) {
                op.setRelevancy(0);
            }
            if (op.getRelevancy() != 0) {
                everyRelevancy.add(op.getRelevancy());
            }
        }

        for (OptionDto optionDto : options) {
            if (optionDto.isCorrect()) {
                numCorrectOptions++;
            } else {
                optionDto.setRelevancy(0);
            }
            for (OptionDto op : options) {
                if (!op.isCorrect())
                    op.setRelevancy(0);
                if (!op.isCorrect() && op.getRelevancy() != 0)
                    throw new TutorException(INVALID_RELEVANCY_ASSIGNMENT);
                if (op.getRelevancy() != 0)
                    everyRelevancy.add(op.getRelevancy());
            }

            optionDto.setSequence(index++);
            new Option(optionDto).setQuestionDetails(this); //add option to question, question details - question of option info
        }
        handleExceptions(numCorrectOptions, options);
        setThisOptions(options);
    }

    public void setThisOptions(List<OptionDto> newOptions) {
        this.options.clear();
        int index = 0;
        for (OptionDto newOp : newOptions) {
            Option op = new Option(newOp);
            op.setSequence(index);
            op.setQuestionDetails(this); //add option to question, question details - question of option info
            index++;
        }
    }

    public void handleExceptions(int numCorrectOptions, List<OptionDto> options) {
        /* Checking for exceptions */
        // INVALID_RELEVANCY_ORDER
        // DUPLICATED_RELEVANCY
        // INVALID_RELEVANCY_ASSIGNMENT
        // MUST_ASSIGN_RELEVANCY

        List<Integer> everyRelevancy = getEveryRelevancy(options);
        Collections.sort(everyRelevancy);

        for (Option opt : getOptions()) {
            if (numCorrectOptions > 1) {
                if (opt.isCorrect() && opt.getRelevancy() == 0 && numCorrectOptions > 1)
                    throw new TutorException(MUST_ASSIGN_RELEVANCY);
                if ((Collections.frequency(everyRelevancy, opt.getRelevancy())) > 1 && numCorrectOptions > 1 && opt.getRelevancy() != 0)
                    throw new TutorException(DUPLICATED_RELEVANCY);

            }
        }
        int check = 1;
        if (everyRelevancy.size() > 1) {
            for (Integer rel : everyRelevancy) {
                if (rel != 0) {
                    if (rel != check) {
                        throw new TutorException(INVALID_RELEVANCY_ORDER);
                    } else {
                        check++;
                    }
                }
            }
        }
    }

    public List<Integer> getEveryRelevancy(List<OptionDto> options) {
        List<Integer> allRelevancy = new ArrayList<>();
        for (OptionDto op : options) {
            if (op.getRelevancy() != 0) {
                allRelevancy.add(op.getRelevancy());
            }
        }
        return allRelevancy;
    }

    public void addOption(Option option) {
        options.add(option);
    }


    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public boolean hasCorrectOptions() {
        for (Option option : this.getOptions()) {
            if (option.isCorrect()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> getCorrectOptionsId() {
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toList());
    }

    public void update(MultipleChoiceQuestionDto questionDetails) {
        setOptions(questionDetails.getOptions());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return convertSequenceToLetter(this.getCorrectAnswer());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitOptions(Visitor visitor) {
        for (Option option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new MultipleChoiceCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new MultipleChoiceStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new MultipleChoiceQuestionDto(this);
    }

    public Integer getCorrectAnswer() {
        return this.getOptions()
                .stream()
                .filter(Option::isCorrect)
                .findAny().orElseThrow(() -> new TutorException(NO_CORRECT_OPTION))
                .getSequence();
    }

    @Override
    public void delete() {
        super.delete();
        for (Option option : this.options) {
            option.remove();
        }
        this.options.clear();
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestion{" +
                "options=" + options +
                '}';
    }

    public static String convertSequenceToLetter(Integer correctAnswer) {
        return correctAnswer != null ? Character.toString('A' + correctAnswer) : "-";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        var result = this.options
                .stream()
                .filter(x -> selectedIds.contains(x.getId()))
                .map(x -> convertSequenceToLetter(x.getSequence()))
                .collect(Collectors.joining("|"));
        return !result.isEmpty() ? result : "-";
    }
}
