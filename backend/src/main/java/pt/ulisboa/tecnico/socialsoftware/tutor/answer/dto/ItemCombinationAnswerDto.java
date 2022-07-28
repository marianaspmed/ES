package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationAnswerDto extends AnswerDetailsDto {
    private List<ItemCombinationAnswerOptionDto> optionDtos = new ArrayList<>();

    public ItemCombinationAnswerDto() {
    }

    public ItemCombinationAnswerDto(ItemCombinationAnswer answer) {
        if (answer.getItemCombinationAnswerOptions() != null) {
            this.optionDtos = answer.getItemCombinationAnswerOptions().stream().map(ItemCombinationAnswerOptionDto::new).collect(Collectors.toList());
        }
    }

    public List<ItemCombinationAnswerOptionDto> getOptionDtos() {
        return optionDtos;
    }

    public void setOptionDtos(List<ItemCombinationAnswerOptionDto> optionDtos) {
        this.optionDtos = optionDtos;
    }
}
