package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer;

public class OpenEndedAnswerDto extends AnswerDetailsDto {
    private String studentAnswer = "";
    private String regex = "";

    public OpenEndedAnswerDto() {
    }

    public OpenEndedAnswerDto(OpenEndedAnswer answer) {
        if (answer.getStudentAnswer() != null && !answer.getStudentAnswer().equals(""))
            this.studentAnswer = answer.getStudentAnswer();
            this.regex = answer.getRegex();
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
