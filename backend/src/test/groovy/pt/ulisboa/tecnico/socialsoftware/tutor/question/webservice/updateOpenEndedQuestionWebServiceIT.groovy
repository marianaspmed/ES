package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateOpenEndedQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port


    def teacher
    def student
    def questionDto
    def question
    def questionId

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()


        teacher = new User(USER_4_NAME, USER_4_EMAIL, USER_4_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.addCourse(externalCourseExecution)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        question = new OpenEndedQuestionDto()
        question.setRegex(REGEX_1)
        question.setAnswerField(ANSWER_1)

        questionDto.setQuestionDetailsDto(question)

        //Teacher Login
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        def mapper = new ObjectMapper()
        def res = restClient.post(
            path: '/courses/' + externalCourse.getId() + '/questions',
            body: mapper.writeValueAsString(questionDto),
            requestContentType: 'application/json'
        )

        questionId = res.data.id

    }

    def "update valid pra question"() {
        given: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and:"an update to the questionDto"
        questionDto.setContent(QUESTION_2_CONTENT)
        question.setRegex(REGEX_2)
        question.setAnswerField(ANSWER_2)


        questionDto.setQuestionDetailsDto(question)


        when:
        def mapper = new ObjectMapper()
        def response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200

        and: "if it responds with the updated question"
        def questionResponse = response.data
        questionResponse.id != null

        questionResponse.content == questionDto.getContent()
        questionResponse.title == questionDto.getTitle()
        questionResponse.status == questionDto.getStatus()
        questionResponse.questionDetailsDto.regex == questionDto.getQuestionDetailsDto().getRegex()
        questionResponse.questionDetailsDto.answerField == questionDto.getQuestionDetailsDto().getAnswerField()

        questionRepository.count() == 1L

        and: "the correct question is inside the repository"
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_2_CONTENT

        def resAnswerField = result.getQuestionDetails().getAnswerField()
        def resRegex = result.getQuestionDetails().getRegex()
        resAnswerField == ANSWER_2
        resRegex == REGEX_2

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())

    }

    def "update pra question as student"() {
        given: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)


        and:"an update to the questionDto"
        questionDto.setContent(QUESTION_2_CONTENT)
        question.setRegex(REGEX_2)
        question.setAnswerField(ANSWER_2)


        questionDto.setQuestionDetailsDto(question)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        questionRepository.count() == 1L

        and: "the correct question is inside the repository"
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        def resAnswerField = result.getQuestionDetails().getAnswerField()
        def resRegex = result.getQuestionDetails().getRegex()
        resAnswerField == ANSWER_1
        resRegex == REGEX_1

        cleanup:
        questionRepository.delete(questionRepository.findById(questionId).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())
    }


    /* TODO Deviam ter control access para teacher não autorizado
    *  */

    def cleanup() {
        userRepository.delete(student)
        userRepository.delete(teacher)
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }

}