package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerOption
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetSolvedQuizzesWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def student
    def quiz

    def pciQuestion
    def pciLeftOption
    def pciRightOption

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        student = new User(USER_5_NAME, USER_5_EMAIL, USER_5_EMAIL, User.Role.STUDENT,
                false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)


        pciQuestion = new Question()
        pciQuestion.setKey(1)
        pciQuestion.setCourse(externalCourse)
        pciQuestion.setContent("Question Content")
        pciQuestion.setTitle("Question Title")

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


        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(null)
        quiz.setResultsDate(null)
        quiz.setCourseExecution(externalCourseExecution)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(pciQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(student)
        quizAnswer.setQuiz(quiz)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)

        def answerDetails = new ItemCombinationAnswer(questionAnswer)
        def leftCorrs = new ArrayList<Integer>()
        leftCorrs.add(0)
        def leftAnswerOption = new ItemCombinationAnswerOption(pciLeftOption, answerDetails, leftCorrs)
        def rightCorrs = new ArrayList<Integer>()
        rightCorrs.add(0)
        def rightAnswerOption = new ItemCombinationAnswerOption(pciRightOption, answerDetails, rightCorrs)
        answerDetails.getItemCombinationAnswerOptions().add(leftAnswerOption)
        answerDetails.getItemCombinationAnswerOptions().add(rightAnswerOption)

        questionAnswer.setAnswerDetails(answerDetails);


        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
    }

    def "get a valid quiz with pci questions"() {
        given: "a logged in teacher"
        createdUserLogin(USER_5_EMAIL, USER_1_PASSWORD)

        when:
        def response = restClient.get(
                path: '/executions/' + externalCourseExecution.getId() + '/quizzes/solved',
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()).get())

    }

    def cleanup() {
        quizAnswerRepository.deleteAll()
        userRepository.delete(student)
        quizRepository.deleteAll()
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }
}