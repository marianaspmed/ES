package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedQuestion extends QuestionDetails {

    // TODO Campo devia ser non nullable. Assim há a possibilidade de ser criado na base de dados a null
    @Column(columnDefinition = "TEXT", name = "answerField" /*, nullable = false*/)
    private String answerField = "answerField"; // TODO Não ter estes valores como padrão


    @Column(columnDefinition = "TEXT", name = "regex"/*, nullable = false*/)
    private String regex = "regex";


    public OpenEndedQuestion() {
        super();
    }

    public OpenEndedQuestion(Question question, OpenEndedQuestionDto questionDto) {
        super(question);
        setAnswerField(questionDto.getAnswerField());
        setRegex(questionDto.getRegex());
    }

    public String getAnswerField() {
        return answerField;
    }

    public String getRegex() {
        return regex;
    }

    public void setAnswerField(String answerField){
        if(answerField == null || answerField.equals("")){
            throw new TutorException(ANSWER_IS_NULL);
        }
        this.answerField = answerField;
    }

    public void setRegex(String regex){
        if(regex.equals(null) || regex.equals("")){
            throw new TutorException(REGEX_IS_NULL);
        }
        this.regex = regex;
    }

    public void update(OpenEndedQuestionDto questionDetails) {
        setAnswerField(questionDetails.getAnswerField());
        setRegex(questionDetails.getRegex());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return getRegex();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new OpenEndedCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new OpenEndedStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new OpenEndedStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new OpenEndedAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenEndedQuestionDto(this);
    }

    @Override
    public String toString() {
        return "OpenEndedQuestion{" +
                "answerField=" + answerField +
                ", regex=" + regex +
                '}';
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return getAnswerField();
    }
}
