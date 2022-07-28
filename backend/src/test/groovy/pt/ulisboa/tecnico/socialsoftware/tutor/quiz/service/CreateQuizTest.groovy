package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateQuizTest extends SpockTest {
    def quizDto
    def questionDto
    def praQuestionDto
    def praQuestion
    def pciQuestionDto
    def pciQuestion

    def setup() {
        createExternalCourseAndExecution()

        quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)

        Question question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle(QUESTION_1_TITLE)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setSequence(1)

        pciQuestion = new Question()
        pciQuestion.setKey(1)
        pciQuestion.setCourse(externalCourse)
        pciQuestion.setTitle(QUESTION_1_TITLE)
        pciQuestion.setContent(QUESTION_1_CONTENT)

        def pciQuestionDetails = new ItemCombinationQuestion()

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

        pciQuestionDto = new QuestionDto(pciQuestion)
        pciQuestionDto.setKey(1)
        pciQuestionDto.setSequence(1)

        //PRA
        praQuestion = new Question()
        praQuestion.setKey(10)
        praQuestion.setCourse(externalCourse)
        praQuestion.setTitle(QUESTION_1_TITLE)
        praQuestion.setContent(QUESTION_1_CONTENT)

        def praQuestionDetails = new OpenEndedQuestion()

        praQuestionDetails.setAnswerField(ANSWER_1)
        praQuestionDetails.setRegex(REGEX_1)


        praQuestion.setQuestionDetails(praQuestionDetails)

        questionDetailsRepository.save(praQuestionDetails)
        questionRepository.save(praQuestion)

        praQuestionDto = new QuestionDto(praQuestion)
        praQuestionDto.setKey(10)
        praQuestionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quizDto.setQuestions(questions)
    }

    @Unroll
    def "valid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quizDto'
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getScramble()
        result.isOneWay()
        result.isQrCodeOnly()
        result.getTitle() == title
        result.getCreationDate() != null
        result.getAvailableDate() == DateHandler.toLocalDateTime(availableDate)
        result.getConclusionDate() == DateHandler.toLocalDateTime(conclusionDate)
        if (resultsDate == null) {
            result.getResultsDate() == DateHandler.toLocalDateTime(conclusionDate)
        } else {
            result.getResultsDate() == DateHandler.toLocalDateTime(resultsDate)
        }
        result.getType() == quizType
        result.getQuizQuestionsNumber() == 1

        where:
        quizType                | title      | availableDate               | conclusionDate       | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | "2020-04-22T02:03:00+01:00" | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | null
    }

    @Unroll
    def "pci quiz with valid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quizDto'
        def questions = new ArrayList()
        questions.add(pciQuestionDto)
        quizDto.setQuestions(questions)
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getScramble()
        result.isOneWay()
        result.isQrCodeOnly()
        result.getTitle() == title
        result.getCreationDate() != null
        result.getAvailableDate() == DateHandler.toLocalDateTime(availableDate)
        result.getConclusionDate() == DateHandler.toLocalDateTime(conclusionDate)
        if (resultsDate == null) {
            result.getResultsDate() == DateHandler.toLocalDateTime(conclusionDate)
        } else {
            result.getResultsDate() == DateHandler.toLocalDateTime(resultsDate)
        }
        result.getType() == quizType
        result.getQuizQuestionsNumber() == 1

        result.getQuizQuestions().get(0).getQuestion().equals(pciQuestion)

        where:
        quizType                | title      | availableDate               | conclusionDate       | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | "2020-04-22T02:03:00+01:00" | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | null
    }

    @Unroll
    def "pra quiz with valid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quizDto'
        def questions = new ArrayList()
        questions.add(praQuestionDto)
        quizDto.setQuestions(questions)
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getScramble()
        result.isOneWay()
        result.isQrCodeOnly()
        result.getTitle() == title
        result.getCreationDate() != null
        result.getAvailableDate() == DateHandler.toLocalDateTime(availableDate)
        result.getConclusionDate() == DateHandler.toLocalDateTime(conclusionDate)
        if (resultsDate == null) {
            result.getResultsDate() == DateHandler.toLocalDateTime(conclusionDate)
        } else {
            result.getResultsDate() == DateHandler.toLocalDateTime(resultsDate)
        }
        result.getType() == quizType
        result.getQuizQuestionsNumber() == 1

        result.getQuizQuestions().get(0).getQuestion().equals(praQuestion)

        where:
        quizType                | title      | availableDate               | conclusionDate       | resultsDate
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | "2020-04-22T02:03:00+01:00" | STRING_DATE_TOMORROW | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | null
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY           | null                 | STRING_DATE_LATER
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | STRING_DATE_TODAY           | STRING_DATE_TOMORROW | null
    }

    @Unroll
    def "invalid arguments: quizType=#quizType | title=#title | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate || errorMessage=#errorMessage "() {
        given: 'a quizDto'
        quizDto.setTitle(title)
        quizDto.setAvailableDate(availableDate)
        quizDto.setConclusionDate(conclusionDate)
        quizDto.setResultsDate(resultsDate)
        quizDto.setType(quizType.toString())

        when:
        quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        quizRepository.count() == 0L

        where:
        quizType                | title      | availableDate        | conclusionDate       | resultsDate          || errorMessage
        null                    | QUIZ_TITLE | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_TYPE_FOR_QUIZ
        "   "                   | QUIZ_TITLE | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_TYPE_FOR_QUIZ
        "Açores"                | QUIZ_TITLE | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_TYPE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | null       | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | "        " | STRING_DATE_TODAY    | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_TITLE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | null                 | STRING_DATE_TOMORROW | STRING_DATE_LATER    || INVALID_AVAILABLE_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TOMORROW | STRING_DATE_TODAY    | STRING_DATE_LATER    || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.IN_CLASS  | QUIZ_TITLE | STRING_DATE_TODAY    | null                 | STRING_DATE_TOMORROW || INVALID_CONCLUSION_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TODAY    | STRING_DATE_LATER    | STRING_DATE_TOMORROW || INVALID_RESULTS_DATE_FOR_QUIZ
        Quiz.QuizType.PROPOSED  | QUIZ_TITLE | STRING_DATE_TOMORROW | STRING_DATE_LATER    | STRING_DATE_TODAY    || INVALID_RESULTS_DATE_FOR_QUIZ
    }

    def "create quiz with wrong question sequence"() {
        given: 'createQuiz a quiz'
        quizDto.setTitle(QUIZ_TITLE)
        quizDto.setType(Quiz.QuizType.GENERATED.toString())
        questionDto.setSequence(3)

        when:
        quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == INVALID_QUESTION_SEQUENCE_FOR_QUIZ
        quizRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
