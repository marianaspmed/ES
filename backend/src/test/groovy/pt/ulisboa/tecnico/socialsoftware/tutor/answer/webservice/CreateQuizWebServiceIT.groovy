package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuizWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def teacher
    def quizDto
    def pciQuestion
    def pciQuestionDetails

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

        pciQuestion = new Question()
        pciQuestion.setKey(1)
        pciQuestion.setCourse(externalCourse)
        pciQuestion.setTitle(QUESTION_1_TITLE)
        pciQuestion.setContent(QUESTION_1_CONTENT)

        pciQuestionDetails = new ItemCombinationQuestion()

        def leftOption = new ItemCombinationOption()
        def rightOption = new ItemCombinationOption()

        leftOption.setContent(OPTION_1_CONTENT)
        leftOption.setSequence(0)
        leftOption.setSide(ItemCombinationOption.SIDE.LEFT)

        rightOption.setContent(OPTION_1_CONTENT)
        rightOption.setSequence(0)
        rightOption.setSide(ItemCombinationOption.SIDE.RIGHT)

        leftOption.getCorrespondences().add(rightOption)
        rightOption.getCorrespondences().add(leftOption)

        leftOption.setQuestionDetails(pciQuestionDetails)
        rightOption.setQuestionDetails(pciQuestionDetails)

        pciQuestion.setQuestionDetails(pciQuestionDetails)

        questionDetailsRepository.save(pciQuestionDetails)
        questionRepository.save(pciQuestion)

        def pciQuestionDto = new QuestionDto(pciQuestion)
        pciQuestionDto.setKey(1)
        pciQuestionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(pciQuestionDto)
        quizDto.setQuestions(questions)

    }

    def "create valid quiz with pci questions: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
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
        responseQuiz.questions.get(0).id == pciQuestion.getId()

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