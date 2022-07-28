package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def student
    def teacher
    def leftOptionDto
    def rightOptionDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        teacher = new User(USER_4_NAME, USER_4_EMAIL, USER_4_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)

    }

    def "create valid pci question"() {
        given: "a questionDto"
        QuestionDto questionDto = createValidPCIQuestionDto()

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
        response.status == 200

        and: "the question data is correct"
        def responseQuestion = response.data
        responseQuestion.id != null
        responseQuestion.title == QUESTION_1_TITLE
        responseQuestion.content == QUESTION_1_CONTENT
        responseQuestion.status == Question.Status.AVAILABLE.name()
        def responseQuestionDetails = responseQuestion.questionDetailsDto
        responseQuestionDetails.type == "item_combination"
        responseQuestionDetails.options.get(0).content == OPTION_2_CONTENT
        responseQuestionDetails.options.get(0).side == ItemCombinationOption.SIDE.RIGHT.name()
        responseQuestionDetails.options.get(0).correspondences.get(0) == leftOptionDto.getSequence()
        responseQuestionDetails.options.get(1).content == OPTION_1_CONTENT
        responseQuestionDetails.options.get(1).side == ItemCombinationOption.SIDE.LEFT.name()
        responseQuestionDetails.options.get(1).correspondences.get(0) == rightOptionDto.getSequence()

        cleanup:
        questionRepository.delete(questionRepository.findById(response.data.id).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }


    def "create a pci question as a student"(){
        given: "a questionDto"
        def questionDto = createValidPCIQuestionDto()

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
    }

    private QuestionDto createValidPCIQuestionDto() {
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        leftOptionDto = new ItemCombinationOptionDto()
        rightOptionDto = new ItemCombinationOptionDto()

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

    def "teacher creates a pem question"(){
        given: "a teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'two correct options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(2)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: "the web service is invoked"
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/courses/' +externalCourse.getId()+ '/questions',
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200

        and: "if it responds with the correct question"
        def question = response.data
        question.title == QUESTION_1_TITLE
        question.status == Question.Status.AVAILABLE.name()
        question.content == QUESTION_1_CONTENT

        question.questionDetailsDto.options.size() == 2
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

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findByKey(teacher.getKey()).get())
        questionRepository.delete(questionRepository.findById(response.data.id).get())

    }

    def "cannot create a pem question as a student"(){
        given: "a student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'two correct options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(2)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
    }

    def cleanup() {
        userRepository.delete(student)
        userRepository.delete(teacher)
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}