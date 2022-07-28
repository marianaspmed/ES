package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetSolvedPRAQuizzesWebserviceIT extends SpockTest {

    @LocalServerPort
    private int port

    def student
    def quiz

    def praQuestion

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT,
                false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)


        praQuestion = new Question()
        praQuestion.setKey(1)
        praQuestion.setCourse(externalCourse)
        praQuestion.setContent("Question Content")
        praQuestion.setTitle("Question Title")

        def praQuestionDetails = new OpenEndedQuestion()

        praQuestionDetails.setAnswerField(ANSWER_1)
        praQuestionDetails.setRegex(REGEX_1)

        praQuestion.setQuestionDetails(praQuestionDetails)

        questionDetailsRepository.save(praQuestionDetails)
        questionRepository.save(praQuestion)


        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(null)
        quiz.setResultsDate(null)
        quiz.setCourseExecution(externalCourseExecution)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(praQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(student)
        quizAnswer.setQuiz(quiz)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)

        def answerDetails = new OpenEndedAnswer(questionAnswer)
        answerDetails.setStudentAnswer("A Student Answer")

        questionAnswer.setAnswerDetails(answerDetails);

        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
    }

    def "get a valid quiz with pra questions"() {
        given: "a logged in teacher"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def response = restClient.get(
                path: '/executions/' + externalCourseExecution.getId() + '/quizzes/solved',
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        and: "the correct number of answers"
        questionAnswerRepository.findAll().size() == 1


        and: 'the return value is OK'
        def quizzesResponse = response.data
        quizzesResponse.size() == 1
        quizzesResponse.statementQuiz.answers[0].answerDetails.studentAnswer[0] == "A Student Answer"

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())

    }

    def cleanup() {
        quizAnswerRepository.deleteAll()
        userRepository.delete(student)
        quizRepository.deleteAll()
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}