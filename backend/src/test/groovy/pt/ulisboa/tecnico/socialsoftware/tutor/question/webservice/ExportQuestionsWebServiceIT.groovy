package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
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
class ExportQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def teacher
    def student
    def originalQuestionDto
    def thePemDto

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

        originalQuestionDto = createValidPCIQuestionDto()

        //pem question
        def pemQuestionDto = new QuestionDto()
        pemQuestionDto.setKey(1)
        pemQuestionDto.setTitle(QUESTION_1_TITLE)
        pemQuestionDto.setContent(QUESTION_1_CONTENT)
        pemQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        pemQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'three options'
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
        thePemDto = questionService.createQuestion(externalCourse.getId(), pemQuestionDto)

    }


    def "export a PCI Question"() {
        given: "A new question dto"
        def receivedDto = questionService.createQuestion(externalCourse.getId(), originalQuestionDto)

        and: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and: "prepare request response"
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when: "the web service is invoked"
        def map = restClient.get(
                path: '/courses/' + externalCourse.getId() + '/questions/export',
                requestContentType: "application/json"
        )

        then: "the response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null

        cleanup:
        questionRepository.delete(questionRepository.findById(receivedDto.getId()).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
    }

    def "export a PCI Question as a student"() {
        given: "A new question dto"
        def receivedDto = questionService.createQuestion(externalCourse.getId(), originalQuestionDto)

        and: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        and: "prepare request response"
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when: "the web service is invoked"
        def map = restClient.get(
                path: '/courses/' + externalCourse.getId() + '/questions/export',
                requestContentType: "application/json"
        )

        then: "the response status is OK"
        assert map['response'].status == 403
        assert map['reader'] != null

        cleanup:
        questionRepository.delete(questionRepository.findById(receivedDto.getId()).get())
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
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


    def "export pem question as a teacher"() {
        given: "a logged in teacher"
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        and: "prepare request response"
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: "/courses/" + externalCourse.getId() + "/questions/export",
                requestContentType: "application/json"
        )

        then: "the response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
        questionRepository.delete(questionRepository.findById(thePemDto.getId()).get())
    }

    def "cannot export pem question as a student"() {
        given: "a logged in student"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        and: "prepare request response"
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: "/courses/" + externalCourse.getId() + "/questions/export",
                requestContentType: "application/json"
        )

        then: "the response status is 403"
        assert map['response'].status == 403
        assert map['reader'] != null

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
        questionRepository.delete(questionRepository.findById(thePemDto.getId()).get())
    }

    def cleanup() {
        userRepository.delete(student)
        userRepository.delete(teacher)
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}