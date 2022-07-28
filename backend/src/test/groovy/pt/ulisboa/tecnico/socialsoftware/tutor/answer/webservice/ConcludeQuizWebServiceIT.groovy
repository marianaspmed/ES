package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationOptionStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcludeQuizWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def student
    def teacher
    def noAccessStudent
    def pciQuiz
    def pciLeftOption
    def pciRightOption
    def pciQuizQuestion
    def pciQuizAnswer
    def pciGeneratedQuiz
    def praQuiz
    def praQuizQuestion
    def praQuizAnswer
    def praGeneratedQuiz

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT,
                false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)

        noAccessStudent = new User(USER_5_NAME, USER_2_EMAIL, USER_2_EMAIL, User.Role.STUDENT,
                false, AuthUser.Type.EXTERNAL)
        noAccessStudent.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(noAccessStudent)

        teacher = new User(USER_4_NAME, USER_4_EMAIL, USER_4_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        // PCI
        def pciQuestion = new Question()
        pciQuestion.setKey(1)
        pciQuestion.setTitle("Question Title")
        pciQuestion.setCourse(externalCourse)


        def pciQuestionDetails = new ItemCombinationQuestion()

        pciLeftOption = new ItemCombinationOption()
        pciRightOption = new ItemCombinationOption()

        pciLeftOption.setContent(OPTION_1_CONTENT)
        pciLeftOption.setSequence(0)
        pciLeftOption.setSide(ItemCombinationOption.SIDE.LEFT)

        pciRightOption.setContent(OPTION_1_CONTENT)
        pciRightOption.setSequence(0)
        pciRightOption.setSide(ItemCombinationOption.SIDE.RIGHT)

        pciLeftOption.getCorrespondences().add(pciRightOption)
        pciRightOption.getCorrespondences().add(pciLeftOption)

        pciLeftOption.setQuestionDetails(pciQuestionDetails)
        pciRightOption.setQuestionDetails(pciQuestionDetails)

        pciQuestion.setQuestionDetails(pciQuestionDetails)

        questionDetailsRepository.save(pciQuestionDetails)
        questionRepository.save(pciQuestion)

        pciQuiz = new Quiz()
        pciQuiz.setKey(1)
        pciQuiz.setTitle("PCI Quiz Title")
        pciQuiz.setType(Quiz.QuizType.PROPOSED.toString())
        pciQuiz.setCourseExecution(externalCourseExecution)
        pciQuiz.setAvailableDate(DateHandler.now())
        pciQuiz.setConclusionDate(DateHandler.now().plusDays(2))
        quizRepository.save(pciQuiz)

        pciQuizQuestion = new QuizQuestion(pciQuiz, pciQuestion, 0)
        quizQuestionRepository.save(pciQuizQuestion)

        pciQuizAnswer = new QuizAnswer(student, pciQuiz)
        quizAnswerRepository.save(pciQuizAnswer)

        // pra
        def praQuestion = new Question()
        praQuestion.setKey(1)
        praQuestion.setCourse(externalCourse)
        praQuestion.setTitle(QUESTION_1_TITLE)
        praQuestion.setContent(QUESTION_1_CONTENT)


        def praQuestionDetails = new OpenEndedQuestion()

        praQuestionDetails.setAnswerField(ANSWER_1)
        praQuestionDetails.setRegex(REGEX_1)

        praQuestion.setQuestionDetails(praQuestionDetails)

        questionDetailsRepository.save(praQuestionDetails)
        questionRepository.save(praQuestion)

        praQuiz = new Quiz()
        praQuiz.setKey(1)
        praQuiz.setTitle("PCI Quiz Title")
        praQuiz.setType(Quiz.QuizType.PROPOSED.toString())
        praQuiz.setCourseExecution(externalCourseExecution)
        praQuiz.setAvailableDate(DateHandler.now())
        praQuiz.setConclusionDate(DateHandler.now().plusDays(2))
        quizRepository.save(praQuiz)

        praQuizQuestion = new QuizQuestion(praQuiz, praQuestion, 0)
        quizQuestionRepository.save(praQuizQuestion)

        praQuizAnswer = new QuizAnswer(student, praQuiz)
        quizAnswerRepository.save(praQuizAnswer)

    }

    def "conclude quiz with pci question with answer, before conclusionDate"() {
        given: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = pciQuiz.getId()
        statementQuizDto.quizAnswerId = pciQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def correspondences = new ArrayList<Integer>()
        correspondences.add(0)

        def pciLeftOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciLeftOption.getId(),ItemCombinationOption.SIDE.LEFT.name(), correspondences)
        def pciRightOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciRightOption.getId(),ItemCombinationOption.SIDE.RIGHT.name(), correspondences)

        itemCombinationAnswerDto.getOptions().add(pciLeftOptionDto)
        itemCombinationAnswerDto.getOptions().add(pciRightOptionDto)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(pciQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        and: 'a logged in student'
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/quizzes/' + pciQuiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        and: "the value is createQuestion and persistent"
        questionAnswerRepository.findAll().size() == 2
        def questionAnswer = questionAnswerRepository.findAll().get(1)
        questionAnswer.getQuizAnswer().isCompleted()
        questionAnswer.getQuizAnswer().getId() == pciQuizAnswer.getId()
        questionAnswer.getId() == pciQuizAnswer.getQuestionAnswers().get(0).getId()
        questionAnswer.getQuizQuestion().getId() == pciQuizQuestion.getId()
        questionAnswer.getQuizQuestion().getSequence() == pciQuizQuestion.getSequence()

        and: 'the return value is OK'
        def correctAnswers = response.data
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.sequence == 0
        correctAnswerDto.correctAnswerDetails.correctOptions.get(0).correspondences.get(0) == pciRightOption.getSequence()
        correctAnswerDto.correctAnswerDetails.correctOptions.get(1).correspondences.get(0) == pciLeftOption.getSequence()


        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())


    }

    def "teacher tries to conclude a quiz"(){
        given: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = pciQuiz.getId()
        statementQuizDto.quizAnswerId = pciQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def correspondences = new ArrayList<Integer>()
        correspondences.add(0)

        def pciLeftOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciLeftOption.getId(),ItemCombinationOption.SIDE.LEFT.name(), correspondences)
        def pciRightOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciRightOption.getId(),ItemCombinationOption.SIDE.RIGHT.name(), correspondences)

        itemCombinationAnswerDto.getOptions().add(pciLeftOptionDto)
        itemCombinationAnswerDto.getOptions().add(pciRightOptionDto)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(pciQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        and: 'a logged in teacher'
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/quizzes/' + pciQuiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())

    }

    def "student with no access tries to conclude a quiz"(){
        given: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = pciQuiz.getId()
        statementQuizDto.quizAnswerId = pciQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def correspondences = new ArrayList<Integer>()
        correspondences.add(0)

        def pciLeftOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciLeftOption.getId(),ItemCombinationOption.SIDE.LEFT.name(), correspondences)
        def pciRightOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciRightOption.getId(),ItemCombinationOption.SIDE.RIGHT.name(), correspondences)

        itemCombinationAnswerDto.getOptions().add(pciLeftOptionDto)
        itemCombinationAnswerDto.getOptions().add(pciRightOptionDto)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(pciQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        and: 'a logged in student'
        createdUserLogin(USER_2_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/quizzes/' + pciQuiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())

    }

    def "conclude quiz with pra question with answer, before conclusionDate"() {
        given: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = praQuiz.getId()
        statementQuizDto.quizAnswerId = praQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def openEndedAnswerDto = new OpenEndedStatementAnswerDetailsDto()

        openEndedAnswerDto.setStudentAnswer(REGEX_1)

        statementAnswerDto.setAnswerDetails(openEndedAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(praQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        and: 'a logged in student'
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/quizzes/' + praQuiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        and: "the value is createQuestion and persistent"
        questionAnswerRepository.findAll().size() == 2
        def questionAnswer = questionAnswerRepository.findAll().get(1)
        questionAnswer.getQuizAnswer().isCompleted()
        questionAnswer.getQuizAnswer().getId() == praQuizAnswer.getId()
        questionAnswer.getId() == praQuizAnswer.getQuestionAnswers().get(0).getId()
        questionAnswer.getQuizQuestion().getId() == praQuizQuestion.getId()
        questionAnswer.getQuizQuestion().getSequence() == praQuizQuestion.getSequence()

        and: 'the return value is OK'
        def correctAnswers = response.data
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.sequence == 0
        correctAnswerDto.correctAnswerDetails.regex == REGEX_1

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())


    }

    def "teacher tries to conclude a pra quiz"(){
        given: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = praQuiz.getId()
        statementQuizDto.quizAnswerId = praQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def openEndedAnswerDto = new OpenEndedStatementAnswerDetailsDto()

        openEndedAnswerDto.setStudentAnswer(REGEX_1)

        statementAnswerDto.setAnswerDetails(openEndedAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(praQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        and: 'a logged in teacher'
        createdUserLogin(USER_4_EMAIL, USER_1_PASSWORD)

        when:
        def mapper = new ObjectMapper()
        def response = restClient.post(
                path: '/quizzes/' + praQuiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())

    }


    def cleanup() {
        quizAnswerRepository.deleteAll()
        userRepository.delete(student)
        userRepository.delete(noAccessStudent)
        userRepository.delete(teacher)
        quizRepository.deleteAll()
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}