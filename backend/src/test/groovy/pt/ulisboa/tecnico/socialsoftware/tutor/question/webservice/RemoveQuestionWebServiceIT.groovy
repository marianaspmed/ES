package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveQuestionWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def student
    def teacher

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

    def "delete valid pci question"() {
        given: "A saved PCI question"
        def dto = createValidPCIQuestionDto()
        def createdQuestion = questionService.createQuestion(externalCourse.getId(), dto)
        and: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)
        when:
        def response = restClient.delete(
                path: '/questions/' + createdQuestion.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }


    def "delete a pci question as a student"(){
        given: "A saved PCI question"
        def dto = createValidPCIQuestionDto()
        def createdQuestion = questionService.createQuestion(externalCourse.getId(), dto)
        and: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)
        when:
        restClient.delete(
                path: '/questions/' + createdQuestion.getId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        questionRepository.delete(questionRepository.findById(createdQuestion.getId()).get())
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

    def "a teacher removes a pem question"() {
        given: "a teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and: 'a pemQuestion'
        def pemQuestionDto = new QuestionDto()
        pemQuestionDto.setKey(1)
        pemQuestionDto.setTitle(QUESTION_1_TITLE)
        pemQuestionDto.setContent(QUESTION_1_CONTENT)
        pemQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        pemQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'two correct options'
        def option1 = new OptionDto()
        option1.setContent(OPTION_1_CONTENT)
        option1.setCorrect(true)
        option1.setRelevancy(1)
        def options = new ArrayList<OptionDto>()
        options.add(option1)
        def option2 = new OptionDto()
        option2.setContent(OPTION_2_CONTENT)
        option2.setCorrect(true)
        option2.setRelevancy(2)
        options.add(option2)

        def pemQuestion = new MultipleChoiceQuestionDto()
        pemQuestion.setOptions(options)
        pemQuestionDto.setQuestionDetailsDto(pemQuestion)
        def thePemDto = questionService.createQuestion(externalCourse.getId(), pemQuestionDto)

        when: "the web service is invoked"
        def response = restClient.delete(
                path: '/questions/'+thePemDto.getId(),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200

        and: "the question was removed from the database"
        questionRepository.findById(thePemDto.getId()).isEmpty()
    }

    def "a student cannot remove a pem question"() {
        given: "a student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        and: 'a pemQuestion'
        def pemQuestionDto = new QuestionDto()
        pemQuestionDto.setKey(1)
        pemQuestionDto.setTitle(QUESTION_1_TITLE)
        pemQuestionDto.setContent(QUESTION_1_CONTENT)
        pemQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        pemQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'two correct options'
        def option1 = new OptionDto()
        option1.setContent(OPTION_1_CONTENT)
        option1.setCorrect(true)
        option1.setRelevancy(1)
        def options = new ArrayList<OptionDto>()
        options.add(option1)
        def option2 = new OptionDto()
        option2.setContent(OPTION_2_CONTENT)
        option2.setCorrect(true)
        option2.setRelevancy(2)
        options.add(option2)

        def pemQuestion = new MultipleChoiceQuestionDto()
        pemQuestion.setOptions(options)
        pemQuestionDto.setQuestionDetailsDto(pemQuestion)
        def thePemDto = questionService.createQuestion(externalCourse.getId(), pemQuestionDto)

        when: "the web service is invoked"
        def response = restClient.delete(
                path: '/questions/'+thePemDto.getId(),
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