package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<ItemCombinationOptionStatementQuestionDetailsDto> options;

    public ItemCombinationStatementQuestionDetailsDto(ItemCombinationQuestion question) {
        this.options = question.getOptions().stream().map(ItemCombinationOptionStatementQuestionDetailsDto::new).collect(Collectors.toList());
    }


    public List<ItemCombinationOptionStatementQuestionDetailsDto> getOptions() {
        return options;
    }

    public void setOptions(List<ItemCombinationOptionStatementQuestionDetailsDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "ItemCombinationStatementQuestionDetailsDto{" +
                ", options=" + options +
                '}';
    }
}