package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto

@DataJpaTest
class ImportExportQuizzesTest extends SpockTest {
    def quiz
    def pciQuiz
    def praQuiz
    def creationDate
    def availableDate
    def conclusionDate

    def setup() {
        createExternalCourseAndExecution()

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto()
        optionDto.setSequence(1)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto = questionService.createQuestion(externalCourse.getId(), questionDto)


        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(false)
        quizDto.setQrCodeOnly(true)
        quizDto.setOneWay(false)
        quizDto.setTitle(QUIZ_TITLE)
        creationDate = DateHandler.now()
        availableDate = DateHandler.now()
        conclusionDate = DateHandler.now().plusDays(2)
        quizDto.setCreationDate(DateHandler.toISOString(creationDate))
        quizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        quizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        quizDto.setType(Quiz.QuizType.EXAM.toString())
        quiz = quizService.createQuiz(externalCourseExecution.getId(), quizDto)

        quizService.addQuestionToQuiz(questionDto.getId(), quiz.getId())
    }

    def 'export and import quizzes'() {
        given: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzesToXml()
        and: 'delete quiz and quizQuestion'
        print quizzesXml
        quizService.removeQuiz(quiz.getId())

        when:
        quizService.importQuizzesFromXml(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 1
        def quizResult = quizRepository.findAll().get(0)
        quizResult.getKey() == 1
        !quizResult.getScramble()
        quizResult.isQrCodeOnly()
        !quizResult.isOneWay()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getType() == Quiz.QuizType.EXAM
        quizResult.getQuizQuestionsNumber() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getKey() == 1
    }

    def 'export and import pci quiz'() {
        given: 'a pci quiz'

        def pciQuestionDto = new QuestionDto()
        pciQuestionDto.setKey(2)
        pciQuestionDto.setTitle(QUESTION_1_TITLE)
        pciQuestionDto.setContent(QUESTION_1_CONTENT)
        pciQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        pciQuestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'two options'
        def leftOptionDto = new ItemCombinationOptionDto()
        def rightOptionDto = new ItemCombinationOptionDto()

        leftOptionDto.setContent(OPTION_1_CONTENT)
        leftOptionDto.setSequence(0)
        leftOptionDto.setSide(ItemCombinationOption.SIDE.LEFT.name())


        def pciOptions = new ArrayList<ItemCombinationOptionDto>()

        pciOptions.add(leftOptionDto)

        rightOptionDto.setContent(OPTION_2_CONTENT)
        rightOptionDto.setSequence(0)
        rightOptionDto.setSide(ItemCombinationOption.SIDE.RIGHT.name())

        pciOptions.add(rightOptionDto)

        leftOptionDto.getCorrespondences().add(rightOptionDto.getSequence())
        rightOptionDto.getCorrespondences().add(leftOptionDto.getSequence())



        def question = new ItemCombinationQuestionDto()
        question.setOptions(pciOptions)

        pciQuestionDto.setQuestionDetailsDto(question)

        pciQuestionDto = questionService.createQuestion(externalCourse.getId(), pciQuestionDto)

        def pciQuizDto = new QuizDto()
        pciQuizDto.setKey(2)
        pciQuizDto.setScramble(false)
        pciQuizDto.setQrCodeOnly(true)
        pciQuizDto.setOneWay(false)
        pciQuizDto.setTitle(QUIZ_TITLE)
        creationDate = DateHandler.now()
        availableDate = DateHandler.now()
        conclusionDate = DateHandler.now().plusDays(2)
        pciQuizDto.setCreationDate(DateHandler.toISOString(creationDate))
        pciQuizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        pciQuizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        pciQuizDto.setType(Quiz.QuizType.EXAM.toString())
        pciQuiz = quizService.createQuiz(externalCourseExecution.getId(), pciQuizDto)

        quizService.addQuestionToQuiz(pciQuestionDto.getId(), pciQuiz.getId())
        and: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzesToXml()
        and: 'delete quiz and quizQuestion'
        print quizzesXml
        quizService.removeQuiz(pciQuiz.getId())

        when:
        quizService.importQuizzesFromXml(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 3
        def quizResult = quizRepository.findAll().get(2)
        quizResult.getKey() == 2
        !quizResult.getScramble()
        quizResult.isQrCodeOnly()
        !quizResult.isOneWay()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getType() == Quiz.QuizType.EXAM
        quizResult.getQuizQuestionsNumber() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getKey() == 2
    }

    def 'export and import pra quiz'() {
        given: 'a pra quiz'

        def praQuestionDto = new QuestionDto()
        praQuestionDto.setKey(3)
        praQuestionDto.setTitle(QUESTION_1_TITLE)
        praQuestionDto.setContent(QUESTION_1_CONTENT)
        praQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        praQuestionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        def question = new OpenEndedQuestionDto()
        question.setRegex(REGEX_1)
        question.setAnswerField(ANSWER_1)

        praQuestionDto.setQuestionDetailsDto(question)

        praQuestionDto = questionService.createQuestion(externalCourse.getId(), praQuestionDto)

        def praQuizDto = new QuizDto()
        praQuizDto.setKey(2)
        praQuizDto.setScramble(false)
        praQuizDto.setQrCodeOnly(true)
        praQuizDto.setOneWay(false)
        praQuizDto.setTitle(QUIZ_TITLE)
        creationDate = DateHandler.now()
        availableDate = DateHandler.now()
        conclusionDate = DateHandler.now().plusDays(2)
        praQuizDto.setCreationDate(DateHandler.toISOString(creationDate))
        praQuizDto.setAvailableDate(DateHandler.toISOString(availableDate))
        praQuizDto.setConclusionDate(DateHandler.toISOString(conclusionDate))
        praQuizDto.setType(Quiz.QuizType.EXAM.toString())
        praQuiz = quizService.createQuiz(externalCourseExecution.getId(), praQuizDto)

        quizService.addQuestionToQuiz(praQuestionDto.getId(), praQuiz.getId())
        and: 'a xml with a quiz'
        def quizzesXml = quizService.exportQuizzesToXml()
        and: 'delete quiz and quizQuestion'
        print quizzesXml
        quizService.removeQuiz(praQuiz.getId())

        when:
        quizService.importQuizzesFromXml(quizzesXml)

        then:
        quizzesXml != null
        quizRepository.findAll().size() == 3
        def quizResult = quizRepository.findAll().get(2)
        quizResult.getKey() == 2
        !quizResult.getScramble()
        quizResult.isQrCodeOnly()
        !quizResult.isOneWay()
        quizResult.getTitle() == QUIZ_TITLE
        quizResult.getCreationDate() == creationDate
        quizResult.getAvailableDate() == availableDate
        quizResult.getConclusionDate() == conclusionDate
        quizResult.getType() == Quiz.QuizType.EXAM
        quizResult.getQuizQuestionsNumber() == 1
        def quizQuestionResult =  quizResult.getQuizQuestions().stream().findAny().orElse(null)
        quizQuestionResult.getSequence() == 0
        quizQuestionResult.getQuiz() == quizResult
        quizQuestionResult.getQuestion().getKey() == 3
    }

    def 'export quiz to latex'() {
        when:
        def quizzesLatex = quizService.exportQuizzesToLatex(quiz.getId())

        then:
        quizzesLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
