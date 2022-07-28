package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import groovy.json.JsonOutput

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def teacher
    def student
    def originalQuestionDto
    def receivedDto
    def thePemDto
    def option1
    def option2
    def option3

    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        and: 'a teacher'
        teacher = new User(USER_4_NAME, USER_4_EMAIL, USER_4_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: 'a student'
        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)

        originalQuestionDto = createValidPCIQuestionDto()
        receivedDto = questionService.createQuestion(externalCourse.getId(), originalQuestionDto)

        and: 'a pemQuestion'
        def pemQuestionDto = new QuestionDto()
        pemQuestionDto.setKey(1)
        pemQuestionDto.setTitle(QUESTION_1_TITLE)
        pemQuestionDto.setContent(QUESTION_1_CONTENT)
        pemQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        pemQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'three options'
        option1 = new OptionDto()
        option1.setContent(OPTION_1_CONTENT)
        option1.setCorrect(true)
        option1.setRelevancy(1)
        def options = new ArrayList<OptionDto>()
        options.add(option1)
        option2 = new OptionDto()
        option2.setContent(OPTION_2_CONTENT)
        option2.setCorrect(true)
        option2.setRelevancy(2)
        options.add(option2)
        option3 = new OptionDto()
        option3.setContent(OPTION_3_CONTENT)
        option3.setCorrect(false)
        options.add(option3)
        def pemQuestion = new MultipleChoiceQuestionDto()
        pemQuestion.setOptions(options)
        pemQuestionDto.setQuestionDetailsDto(pemQuestion)
        thePemDto = questionService.createQuestion(externalCourse.getId(), pemQuestionDto)

    }

    def "update a PCI Question"() {
        given: "A new question dto"
        receivedDto.setContent("Updated PCI Content")

        and: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)
        when:
        def mapper = new ObjectMapper()
        def response = restClient.put(
                path: '/questions/' + receivedDto.getId(),
                body: mapper.writeValueAsString(receivedDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        and: "the question data is correct"
        def responseQuestion = response.data
        responseQuestion.id != null
        responseQuestion.title == QUESTION_1_TITLE
        responseQuestion.content == receivedDto.getContent()
        responseQuestion.status == Question.Status.AVAILABLE.name()
        def responseQuestionDetails = responseQuestion.questionDetailsDto
        responseQuestionDetails.type == "item_combination"
        responseQuestionDetails.options.get(0).content == OPTION_2_CONTENT
        responseQuestionDetails.options.get(0).side == ItemCombinationOption.SIDE.RIGHT.name()
        responseQuestionDetails.options.get(0).correspondences.get(0) == 0
        responseQuestionDetails.options.get(1).content == OPTION_1_CONTENT
        responseQuestionDetails.options.get(1).side == ItemCombinationOption.SIDE.LEFT.name()
        responseQuestionDetails.options.get(1).correspondences.get(0) == 0

        cleanup:
        questionRepository.delete(questionRepository.findById(response.data.id).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }

    def "student tries to update a question"(){
        given: "A new question dto"
        receivedDto.setContent("Updated PCI Content")

        and: "a logged in teacher"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)
        when:
        def mapper = new ObjectMapper()
        def response = restClient.put(
                path: '/questions/' + receivedDto.getId(),
                body: receivedDto,
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    private QuestionDto createValidPCIQuestionDto() {
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        def leftOptionDto = new ItemCombinationOptionDto()
        def rightOptionDto = new ItemCombinationOptionDto()

        leftOptionDto.setContent(OPTION_1_CONTENT)
        leftOptionDto.setSequence(0)
        leftOptionDto.setSide(ItemCombinationOption.SIDE.LEFT.name())


        rightOptionDto.setContent(OPTION_2_CONTENT)
        rightOptionDto.setSequence(0)
        rightOptionDto.setSide(ItemCombinationOption.SIDE.RIGHT.name())


        leftOptionDto.getCorrespondences().add(rightOptionDto.getSequence())
        rightOptionDto.getCorrespondences().add(leftOptionDto.getSequence())
        def options = new ArrayList<ItemCombinationOptionDto>()


        options.add(rightOptionDto)
        options.add(leftOptionDto)

        def question = new ItemCombinationQuestionDto()
        question.setOptions(options)

        questionDto.setQuestionDetailsDto(question)
        return questionDto
    }

    def "teacher updates a pem question"(){
        given: "a teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and: "a new questionDto"
        thePemDto.setKey(1)
        thePemDto.setTitle(QUESTION_2_TITLE)
        thePemDto.setContent(QUESTION_2_CONTENT)

        when: "the web service is invoked"
        def response = restClient.put(
                path: '/questions/' + thePemDto.getId(),
                body: thePemDto,
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200

        and: "if it responds with the updated question"
        def question = response.data
        question.id != null
        question.title == QUESTION_2_TITLE
        question.status == Question.Status.AVAILABLE.name()
        question.content == QUESTION_2_CONTENT

        question.questionDetailsDto.options.size() == 3
        def questionDetails = question.questionDetailsDto
        questionDetails.type == "multiple_choice"
        def optionOne = questionDetails.options.get(0)
        optionOne.content == OPTION_1_CONTENT
        optionOne.correct
        optionOne.relevancy == 1
        def optionTwo = questionDetails.options.get(1)
        optionTwo.content == OPTION_2_CONTENT
        optionTwo.correct
        optionTwo.relevancy == 2
        def optionThree = questionDetails.options.get(2)
        optionThree.content == OPTION_3_CONTENT
        !optionThree.correct
        optionThree.relevancy == 0

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findByKey(teacher.getKey()).get())
        questionRepository.delete(questionRepository.findById(response.data.id).get())
    }

    def "a student cannot update a pem question"(){
        given: "a student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        and: "a new questionDto"
        thePemDto.setKey(1)
        thePemDto.setTitle(QUESTION_2_TITLE)
        thePemDto.setContent(QUESTION_2_CONTENT)

        when: "the web service is invoked"
        def response = restClient.put(
                path: '/questions/' + thePemDto.getId(),
                body: thePemDto,
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        userRepository.delete(student)
        userRepository.delete(teacher)
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}