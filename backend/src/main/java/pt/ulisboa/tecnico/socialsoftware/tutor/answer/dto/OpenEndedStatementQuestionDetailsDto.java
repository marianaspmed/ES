package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;

public class OpenEndedStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private String answerField;
    private String regex;

    public OpenEndedStatementQuestionDetailsDto(OpenEndedQuestion question) {
        this.answerField = question.getAnswerField();
        this.regex = question.getRegex();
    }

    @Override
    public String toString() {
        return "OpenEndedStatementQuestionDetailsDto{" +
                "answerField=" + answerField +
                "regex=" + regex +
                '}';
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getAnswerField() {
        return answerField;
    }

    public void setAnswerField(String answerField) {
        this.answerField = answerField;
    }
}