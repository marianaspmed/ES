package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
//import javax.persistence.JoinColumn;


@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedAnswer extends AnswerDetails {

    @Column(columnDefinition = "TEXT", name = "studentAnswer" /*, nullable = false*/)
    private String studentAnswer = "";

    @Column(columnDefinition = "TEXT", name = "regex"/*, nullable = false*/)
    private String regex = "";

    public OpenEndedAnswer() {
        super();
    }

    public OpenEndedAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
    }

    public OpenEndedAnswer(QuestionAnswer questionAnswer, String studentAnswer, String regex){
        super(questionAnswer);
        this.setStudentAnswer(studentAnswer);
        this.setRegex(regex);
    }

    public String getStudentAnswer() {
        return this.studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;

    }

    public void setStudentAnswer(OpenEndedQuestion question, OpenEndedStatementAnswerDetailsDto openEndedStatementAnswerDetailsDto) {
        if (openEndedStatementAnswerDetailsDto.getStudentAnswer() != null && !openEndedStatementAnswerDetailsDto.getStudentAnswer().equals("")) {
            this.setStudentAnswer(openEndedStatementAnswerDetailsDto.getStudentAnswer());
        } else {
            this.setStudentAnswer("");
        }
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setRegex(OpenEndedQuestion question, OpenEndedStatementAnswerDetailsDto openEndedStatementAnswerDetailsDto) {
        if (openEndedStatementAnswerDetailsDto.getRegex() != null && !openEndedStatementAnswerDetailsDto.getRegex().equals("")) {
            this.setRegex(openEndedStatementAnswerDetailsDto.getRegex());
        } else {
            this.setRegex("");
        }
    }


    @Override
    public boolean isCorrect() {
        if (this.isAnswered() && this.studentAnswer.equals(this.regex)) {
            return true;
        }
        else {
            return false;
        }
    }


    public void remove() {
        if (this.studentAnswer != null) {
            studentAnswer = "";
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new OpenEndedAnswerDto(this);
    }

    @Override
    public boolean isAnswered() {
        return this.getStudentAnswer() != null && !this.getStudentAnswer().equals("");
    }

    @Override
    public String getAnswerRepresentation() {
        if (this.isAnswered()) {
            return this.getStudentAnswer();
        }
        else {
            return "-";
        }
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new OpenEndedStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }


}
