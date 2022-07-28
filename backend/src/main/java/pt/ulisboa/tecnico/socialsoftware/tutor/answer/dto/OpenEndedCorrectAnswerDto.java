package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;

public class OpenEndedCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private String regex = "";

    public OpenEndedCorrectAnswerDto(OpenEndedQuestion question) {
        this.regex = question.getRegex();
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "MultipleChoiceCorrectAnswerDto{" +
                "regex=" + regex +
                '}';
    }
}