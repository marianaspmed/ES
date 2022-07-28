package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import javax.persistence.Transient;

public class OpenEndedStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private String studentAnswer = "";
    private String regex = "";

    public OpenEndedStatementAnswerDetailsDto() {
    }

    public OpenEndedStatementAnswerDetailsDto(OpenEndedAnswer questionAnswer) {
        if (questionAnswer.getStudentAnswer() != null && !questionAnswer.getStudentAnswer().equals("")) {
            this.studentAnswer = questionAnswer.getStudentAnswer();
            this.regex = questionAnswer.getRegex();
        }
    }

    public String getStudentAnswer() {
        return this.studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Transient
    private OpenEndedAnswer createdOpenEndedAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdOpenEndedAnswer = new OpenEndedAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdOpenEndedAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return studentAnswer == null || studentAnswer.equals("");
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new OpenEndedAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(OpenEndedQuestion question) {
        createdOpenEndedAnswer.setStudentAnswer(question, this);
    }

    @Override
    public String toString() {
        return "OpenEndedStatementAnswerDto{" +
                "studentAnswer=" + studentAnswer +
                '}';
    }

}
