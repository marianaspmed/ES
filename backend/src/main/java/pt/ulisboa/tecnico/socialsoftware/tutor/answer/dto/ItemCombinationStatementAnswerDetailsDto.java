package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<ItemCombinationOptionStatementAnswerDetailsDto> options = new ArrayList<>();

    public ItemCombinationStatementAnswerDetailsDto() {
    }

    public ItemCombinationStatementAnswerDetailsDto(ItemCombinationAnswer questionAnswer) {
        if (questionAnswer.getItemCombinationAnswerOptions() != null) {
            this.options = questionAnswer.getItemCombinationAnswerOptions()
                    .stream()
                    .map(ItemCombinationOptionStatementAnswerDetailsDto::new)
                    .collect(Collectors.toList());
        }
    }

    public List<ItemCombinationOptionStatementAnswerDetailsDto> getOptions() {
        return options;
    }

    public void setOptions(List<ItemCombinationOptionStatementAnswerDetailsDto> options) {
        this.options = options;
    }

    @Transient
    private ItemCombinationAnswer itemCombinationAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        itemCombinationAnswer = new ItemCombinationAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return itemCombinationAnswer;
    }

    @Override
    public void update(ItemCombinationQuestion question) {
        itemCombinationAnswer.setItemCombinationAnswerOptions(question, this);
    }

    @Override
    public boolean emptyAnswer() {
        return options == null || options.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new ItemCombinationAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public String toString() {
        return "ItemCombinationStatementAnswerDetailsDto{" +
                "options=" + options +
                '}';
    }
}
