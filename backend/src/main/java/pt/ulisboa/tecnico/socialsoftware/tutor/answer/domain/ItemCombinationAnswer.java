package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationAnswer extends AnswerDetails {
    @OneToMany(mappedBy = "itemCombinationAnswer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemCombinationAnswerOption> itemCombinationAnswerOptions = new ArrayList<>();

    public ItemCombinationAnswer() {
        super();
    }

    public ItemCombinationAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
    }

    @Override
    public boolean isCorrect() {
        return this.getItemCombinationAnswerOptions().stream().allMatch(ItemCombinationAnswerOption::isCorrect);
    }

    @Override
    public void remove() {
        itemCombinationAnswerOptions.forEach(ItemCombinationAnswerOption::remove);
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new ItemCombinationAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
       return "ITEM_COMBINATION_REPRESENTATION";
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return  !itemCombinationAnswerOptions.isEmpty();
    }

    public List<ItemCombinationAnswerOption> getLeftItemCombinationAnswerOptions() {
        return itemCombinationAnswerOptions.stream().filter((o)->o.getItemCombinationOption().getSide().equals(ItemCombinationOption.SIDE.LEFT)).collect(Collectors.toList());
    }

    public List<ItemCombinationAnswerOption> getItemCombinationAnswerOptions() {
        return itemCombinationAnswerOptions;
    }

    public void setItemCombinationAnswerOptions(ItemCombinationQuestion question,
                                ItemCombinationStatementAnswerDetailsDto itemCombinationStatementAnswerDetailsDto) {
        this.itemCombinationAnswerOptions.clear();
        if (!itemCombinationStatementAnswerDetailsDto.emptyAnswer()) {
            for (var optionDto : itemCombinationStatementAnswerDetailsDto.getOptions()) {

                ItemCombinationOption option = question
                        .getOptionById(optionDto.getOptionId());

                var answerOption = new ItemCombinationAnswerOption(option, this, optionDto.getCorrespondences());
                this.itemCombinationAnswerOptions.add(answerOption);
            }
        }
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}
