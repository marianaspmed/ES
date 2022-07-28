package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval=true)
    private final List<ItemCombinationOption> options = new ArrayList<>();


    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto questionDto){
        super(question);
        setOptions(questionDto);
    }

    public ItemCombinationQuestion() {

    }


    public void setOptions(ItemCombinationQuestionDto question){
        isDtoValid(question);
        List<ItemCombinationOptionDto> dtoOptions = question.getOptions();

        for(ItemCombinationOption opt : this.options){
            opt.remove();
        }
        options.clear();

        for(ItemCombinationOptionDto dto : dtoOptions){
            new ItemCombinationOption(dto).setQuestionDetails(this);
        }

        for(ItemCombinationOptionDto dto : dtoOptions){
            ItemCombinationOption option1 = this.options.stream()
                                                        .filter(o -> o.getSequence().equals(dto.getSequence()))
                                                        .filter(o -> o.getSide().name().equals(dto.getSide()))
                                                        .findFirst().get();
            for (Integer corr : dto.getCorrespondences()){
                option1.addCorrespondence(this.options.stream()
                                                        .filter(o -> o.getSequence().equals(corr))
                                                        .filter(o -> !o.getSide().name().equals(dto.getSide()))
                                                        .findFirst().get());
            }
        }


    }

    private void isDtoValid(ItemCombinationQuestionDto questionDto){
        List<ItemCombinationOptionDto> leftDtoOptions = questionDto.getOptions().stream().filter(o -> o.getSide().equals(ItemCombinationOption.SIDE.LEFT.name())).collect(Collectors.toList());
        List<ItemCombinationOptionDto> rightDtoOptions = questionDto.getOptions().stream().filter(o -> o.getSide().equals(ItemCombinationOption.SIDE.RIGHT.name())).collect(Collectors.toList());

        if(leftDtoOptions.isEmpty() || rightDtoOptions.isEmpty()) {
            throw new TutorException(AT_LEAST_TWO_ITEM_SETS_NEEDED);
        }

        ArrayList<Integer> usedSequenceNumbers = new ArrayList<>();

        for (ItemCombinationOptionDto optionDto : leftDtoOptions){
            if (!usedSequenceNumbers.contains(optionDto.getSequence())){
                usedSequenceNumbers.add(optionDto.getSequence());
            } else {
                throw new TutorException(INVALID_SEQUENCE_FOR_OPTION);
            }
        }

        usedSequenceNumbers.clear();

        for (ItemCombinationOptionDto optionDto : rightDtoOptions){
            if (!usedSequenceNumbers.contains(optionDto.getSequence())){
                usedSequenceNumbers.add(optionDto.getSequence());
            } else {
                throw new TutorException(INVALID_SEQUENCE_FOR_OPTION);
            }
        }


        checkMismatchedCorrespondences(leftDtoOptions, rightDtoOptions);

        checkMismatchedCorrespondences(rightDtoOptions, leftDtoOptions);

        leftDtoOptions.stream()
                .filter(corr -> !corr.getCorrespondences().isEmpty())
                .findAny().
                orElseThrow(() -> new TutorException(AT_LEAST_ONE_CORRESPONDENCE_NEEDED));
        rightDtoOptions.stream()
                .filter(corr -> !corr.getCorrespondences().isEmpty())
                .findAny().
                orElseThrow(() -> new TutorException(AT_LEAST_ONE_CORRESPONDENCE_NEEDED));
    }

    private void checkMismatchedCorrespondences(List<ItemCombinationOptionDto> startingSet, List<ItemCombinationOptionDto> arrivingSet) {
        for (ItemCombinationOptionDto optionDto : startingSet) {
            for (Integer corr : optionDto.getCorrespondences()) {
                arrivingSet.stream()
                        .filter(o -> o.getSequence().equals(corr))
                        .filter(o -> o.getCorrespondences().contains(optionDto.getSequence()))
                        .findAny().
                        orElseThrow(() -> new TutorException(MISMATCHED_CORRESPONDENCES));
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitLeftOptions(Visitor visitor){
        List<ItemCombinationOption> leftOptions = this.options.stream().filter(o -> o.getSide().equals(ItemCombinationOption.SIDE.LEFT)).collect(Collectors.toList());
        for (ItemCombinationOption option : leftOptions) {
            option.accept(visitor);
        }
    }

    public void visitRightOptions(Visitor visitor){
        List<ItemCombinationOption> rightOptions = this.options.stream().filter(o -> o.getSide().equals(ItemCombinationOption.SIDE.RIGHT)).collect(Collectors.toList());
        for (ItemCombinationOption option : rightOptions) {
            option.accept(visitor);
        }
    }


    public void addOption(ItemCombinationOption option){
        this.options.add(option);
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new ItemCombinationCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new ItemCombinationStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new ItemCombinationAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    public void update(ItemCombinationQuestionDto questionDetailsDto){
        setOptions(questionDetailsDto);
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        var result = new StringBuilder();
        List<ItemCombinationOption> leftOptions = this.options.stream().filter(o -> o.getSide().equals(ItemCombinationOption.SIDE.LEFT)).collect(Collectors.toList());
        for (ItemCombinationOption option : leftOptions){
            result.append(option.getSequence()).append(" ->");
            for (ItemCombinationOption correspondence : option.getCorrespondences()) {
                result.append(" ").append(correspondence.getSequence());
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        //TODO this is for exporting, no need so far
        return null;
    }

    public ItemCombinationOption getOptionById(Integer id){
        return this.options.stream().filter(o-> o.getId().equals(id)).findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ORDER_SLOT_MISMATCH, id));
    }

    @Override
    public String toString(){
        return "Item Combination Question: Left: " + this.options.toString();
    }

    public List<ItemCombinationOption> getOptions(){
        return this.options;
    }

}
