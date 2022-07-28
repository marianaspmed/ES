package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedAnswerItem extends QuestionAnswerItem {

    private String studentAnswer = "";
    private String regex = "";

    public OpenEndedAnswerItem() {
    }

    public OpenEndedAnswerItem(String username, int quizId, StatementAnswerDto answer, OpenEndedStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.studentAnswer = detailsDto.getStudentAnswer();
        this.regex = detailsDto.getRegex();
    }

    @Override
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        if (this.studentAnswer != null && this.studentAnswer != "") {
            return this.getStudentAnswer();
        }
        else {
            return "-";
        }
    }

    public String getStudentAnswer() {
        return studentAnswer;
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
}
