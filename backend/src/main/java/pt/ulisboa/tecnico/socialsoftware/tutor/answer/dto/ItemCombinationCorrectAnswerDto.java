package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<ItemCombinationAnswerOptionDto> correctOptions;

    public ItemCombinationCorrectAnswerDto(ItemCombinationQuestion question) {
        this.correctOptions = question.getOptions()
                .stream()
                .map(ItemCombinationAnswerOptionDto::new)
                .collect(Collectors.toList());
        for (ItemCombinationOption option : question.getOptions()){
            ArrayList<Integer> integerCorrespondences = new ArrayList<>();
            for (ItemCombinationOption corr : option.getCorrespondences()){
                integerCorrespondences.add(corr.getSequence());
            }
            for(ItemCombinationAnswerOptionDto optionDto : this.correctOptions){
                if(!integerCorrespondences.isEmpty()
                        && integerCorrespondences.contains(optionDto.getSequence())
                        && !optionDto.getSide().equals(option.getSide().name())){
                    optionDto.getCorrespondences().add(option.getSequence());
                }
            }
        }
    }

    public List<ItemCombinationAnswerOptionDto> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(List<ItemCombinationAnswerOptionDto> correctOptions) {
        this.correctOptions = correctOptions;
    }
}