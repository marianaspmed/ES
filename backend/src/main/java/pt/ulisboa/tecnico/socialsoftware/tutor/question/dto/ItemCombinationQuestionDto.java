package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ItemCombinationQuestionDto extends QuestionDetailsDto{

    private List<ItemCombinationOptionDto> options = new ArrayList<>();

    public ItemCombinationQuestionDto() {
    }

    public ItemCombinationQuestionDto(ItemCombinationQuestion question){
        this.options = question.getOptions().stream().map(ItemCombinationOptionDto::new).collect(Collectors.toList());

        for (ItemCombinationOption option : question.getOptions()){
            ArrayList<Integer> integerCorrespondences = new ArrayList<>();
            for (ItemCombinationOption corr : option.getCorrespondences()){
                integerCorrespondences.add(corr.getSequence());
            }

            for(ItemCombinationOptionDto optionDto : this.options){
                if(!integerCorrespondences.isEmpty()
                        && integerCorrespondences.contains(optionDto.getSequence())
                        && !optionDto.getSide().equals(option.getSide().name())){
                    optionDto.getCorrespondences().add(option.getSequence());
                }
            }
        }
    }

    public List<ItemCombinationOptionDto> getOptions(){
        return this.options;
    }

    public void setOptions(List<ItemCombinationOptionDto> options) {
        this.options = options;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new ItemCombinationQuestion(question, this);
    }

    @Override
    public void update(ItemCombinationQuestion question) {
        question.update(this);
    }

    @Override
    public String toString(){
        return "Item Combination Question DTO: " + options.toString();
    }
}
