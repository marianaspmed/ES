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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenEndedQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port


    def teacher
    def student

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
    }

    def "create valid pra question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        def question = new OpenEndedQuestionDto()
        question.setAnswerField(ANSWER_1)
        question.setRegex(REGEX_1)

        questionDto.setQuestionDetailsDto(question)

        and: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
            path: '/courses/' + externalCourse.getId() + '/questions',
            body: mapper.writeValueAsString(questionDto),
            requestContentType: 'application/json'
        )

        then: "check response status"
        questionRepository.count() == 1L
        response != null
        response.status == 200
        response.data.content == questionDto.getContent()
        response.data.title == questionDto.getTitle()
        response.data.status == questionDto.getStatus()
        response.data.questionDetailsDto.regex == questionDto.getQuestionDetailsDto().getRegex()


        cleanup:
        questionRepository.delete(questionRepository.findById(response.data.id).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }

    def "create a pra question as a student"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        def question = new OpenEndedQuestionDto()
        question.setRegex(REGEX_1)

        questionDto.setQuestionDetailsDto(question)

        and: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/courses/' + externalCourse.getId() + '/questions',
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        questionRepository.count() == 0

        cleanup:
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