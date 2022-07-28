package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuizWebServicePRAIT extends SpockTest {

    @LocalServerPort
    private int port

    def teacher
    def quizDto
    def praQuestion
    def praQuestionDetails

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        teacher = new User(USER_4_NAME, USER_4_EMAIL, USER_4_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        // Create a general quiz
        quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)


        // pra
        praQuestion = new Question()
        praQuestion.setKey(1)
        praQuestion.setCourse(externalCourse)
        praQuestion.setTitle(QUESTION_1_TITLE)
        praQuestion.setContent(QUESTION_1_CONTENT)

        praQuestionDetails = new OpenEndedQuestion()

        praQuestionDetails.setAnswerField(ANSWER_1)
        praQuestionDetails.setRegex(REGEX_1)

        praQuestion.setQuestionDetails(praQuestionDetails)

        questionDetailsRepository.save(praQuestionDetails)
        questionRepository.save(praQuestion)

        def praQuestionDto = new QuestionDto(praQuestion)
        praQuestionDto.setKey(1)
        praQuestionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(praQuestionDto)
        quizDto.setQuestions(questions)

    }

    def "create valid quiz with pra questions: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: "a quizDto"
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        and: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/executions/' + externalCourseExecution.getId() + '/quizzes',
                body: mapper.writeValueAsString(quizDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        and: "the quiz data is correct"
        def responseQuiz = response.data
        responseQuiz.id != null
        responseQuiz.questions.get(0).id == praQuestion.getId()

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())


        where:
        quizType                | title      | availableDate               | conclusionDate       | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | "2020-04-22T02:03:00+01:00" | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | null

    }

    def cleanup() {
        userRepository.delete(teacher)
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
} 