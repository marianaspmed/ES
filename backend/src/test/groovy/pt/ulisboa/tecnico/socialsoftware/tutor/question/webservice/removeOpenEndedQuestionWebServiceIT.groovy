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
class RemoveOpenEndedQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def teacher
    def student
    def questionDto
    def question
    def questionId1
    def questionId2

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

        //First question
        def mapper = new ObjectMapper()
        def res = restClient.post(
            path: '/courses/' + externalCourse.getId() + '/questions',
            body: mapper.writeValueAsString(questionDto),
            requestContentType: 'application/json'
        )

        questionId1 = res.data.id

        //Second Question
        mapper = new ObjectMapper()
        res = restClient.post(
            path: '/courses/' + externalCourse.getId() + '/questions',
            body: mapper.writeValueAsString(questionDto),
            requestContentType: 'application/json'
        )

        questionId2 = res.data.id

    }

    def "remove valid pra question"() {
        given: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.delete(
                path: '/questions/' + questionId1
        )

        then: "check response status"

        /*There are two questions created, we removed one right now, so we should have only one in the repository */
        questionRepository.count() == 1L

        response != null
        response.status == 200

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }

    def "remove pra question as student"() {
        given: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.delete(
                path: '/questions/' + questionId2
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        /*There should still be 2 questions in the repository */
        questionRepository.count() == 2L

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