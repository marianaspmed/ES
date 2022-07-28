package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

public class OpenEndedQuestionDto extends QuestionDetailsDto {
    private String answerField = "";
    private String regex = "";

    public OpenEndedQuestionDto() {
    }

    public OpenEndedQuestionDto(OpenEndedQuestion question) {
        this.answerField = question.getAnswerField();
        this.regex = question.getRegex();
    }

    public String getAnswerField() {
        return answerField;
    }

    public String getRegex() {
        return regex;
    }

    public void setAnswerField(String answerField){
        this.answerField = answerField;
    }

    public void setRegex(String regex){
        this.regex = regex;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenEndedQuestion(question, this);
    }

    @Override
    public void update(OpenEndedQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenEndedQuestion{" +
                "answerField=" + answerField +
                ", regex=" + regex +
                '}';
    }

}
