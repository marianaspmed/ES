package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationOptionStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NO_LONGER_AVAILABLE

@DataJpaTest
class ConcludeQuizTest extends SpockTest {

    // General
    def user
    def quizQuestion
    def optionOk
    def optionKO
    def quizAnswer
    def date
    def quiz

    // pra
    def praQuiz
    def praQuizQuestion
    def praQuizAnswer

    // PCI
    def pciQuiz
    def pciQuizQuestion
    def pciQuizAnswer
    def pciLeftOption
    def pciRightOption

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(0)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        optionOk = new Option()
        optionOk.setContent("Option Content")
        optionOk.setCorrect(true)
        optionOk.setSequence(1)
        optionOk.setQuestionDetails(questionDetails)
        optionRepository.save(optionOk)

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)




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
        quizRepository.save(pciQuiz)


        pciQuizQuestion = new QuizQuestion(pciQuiz, pciQuestion, 0)
        quizQuestionRepository.save(pciQuizQuestion)

        pciQuizAnswer = new QuizAnswer(user, pciQuiz)
        quizAnswerRepository.save(pciQuizAnswer)

        // pra
        def praQuestion = new Question()
        praQuestion.setKey(1)
        praQuestion.setTitle(QUESTION_1_TITLE)
        praQuestion.setCourse(externalCourse)
        praQuestion.setContent(QUESTION_1_CONTENT)
        def praQuestionDetails = new OpenEndedQuestion()

        praQuestionDetails.setAnswerField(ANSWER_1)
        praQuestionDetails.setRegex(REGEX_1)

        praQuestion.setQuestionDetails(praQuestionDetails)

        questionDetailsRepository.save(praQuestionDetails)
        questionRepository.save(praQuestion)
        praQuiz = new Quiz()
        praQuiz.setKey(1)
        praQuiz.setTitle("PRA Quiz Title")
        praQuiz.setType(Quiz.QuizType.PROPOSED.toString())
        praQuiz.setCourseExecution(externalCourseExecution)
        praQuiz.setAvailableDate(DateHandler.now())
        quizRepository.save(praQuiz)

        praQuizQuestion = new QuizQuestion(praQuiz, praQuestion, 0)
        quizQuestionRepository.save(praQuizQuestion)

        praQuizAnswer = new QuizAnswer(user, praQuiz)
        quizAnswerRepository.save(praQuizAnswer)

    }

    def 'conclude quiz without conclusionDate, without answering'() {
        given: 'an empty answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() != null
        questionAnswerRepository.findAll().size() == 3
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getAnswerDetails() == null
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectAnswerDetails().getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz IN_CLASS without answering, before conclusionDate'() {
        given: 'an IN_CLASS quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        and: 'an empty answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() == null
        quizAnswerItemRepository.findAll().size() == 1
        def quizAnswerItem = quizAnswerItemRepository.findAll().get(0)
        quizAnswerItem.getQuizId() == quiz.getId()
        quizAnswerItem.getQuizAnswerId() == quizAnswer.getId()
        quizAnswerItem.getAnswerDate() != null
        quizAnswerItem.getAnswersList().size() == 1
        def resStatementAnswerDto = quizAnswerItem.getAnswersList().get(0)
        resStatementAnswerDto.getAnswerDetails() == null
        resStatementAnswerDto.getSequence() == 0
        resStatementAnswerDto.getTimeTaken() == 100
        and: 'does not return answers'
        correctAnswers == []
    }

    def 'conclude quiz with answer, before conclusionDate'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def multipleChoiceAnswerDto = new MultipleChoiceStatementAnswerDetailsDto()
        multipleChoiceAnswerDto.setOptionId(optionOk.getId())
        statementAnswerDto.setAnswerDetails(multipleChoiceAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 3
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        ((MultipleChoiceAnswer) questionAnswer.getAnswerDetails()).getOption() == optionOk
        optionOk.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectAnswerDetails().getCorrectOptionId() == optionOk.getId()
    }

    def 'conclude quiz without answering, before availableDate'() {
        given: 'a quiz with future availableDate'
        quiz.setAvailableDate(DateHandler.now().plusDays(2))
        and: 'an empty answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()

        when:
        answerService.concludeQuiz(statementQuizDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NOT_YET_AVAILABLE
    }

    def 'conclude quiz without answering, after conclusionDate'() {
        given: 'an IN_CLASS quiz with conclusionDate before now in days'
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setAvailableDate(DateHandler.now().minusDays(2))
        quiz.setConclusionDate(DateHandler.now().minusDays(1))
        and: 'an empty answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()

        when:
        answerService.concludeQuiz(statementQuizDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUIZ_NO_LONGER_AVAILABLE
    }

    def 'conclude quiz without answering, 9 minutes after conclusionDate'() {
        given: 'an IN_CLASS quiz with conclusionDate before now in days'
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setAvailableDate(DateHandler.now().minusDays(2))
        quiz.setConclusionDate(DateHandler.now().minusMinutes(9))
        and: 'an empty answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()

        when:
        answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        quizAnswer.getAnswerDate() == null
        quizAnswerItemRepository.findAll().size() == 1
    }

    def 'conclude completed quiz'() {
        given:  'a completed quiz'
        quizAnswer.completed = true
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def multipleChoiceAnswerDto = new MultipleChoiceStatementAnswerDetailsDto()
        multipleChoiceAnswerDto.setOptionId(optionOk.getId())
        statementAnswerDto.setAnswerDetails(multipleChoiceAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'nothing occurs'
        quizAnswer.getAnswerDate() == null
        questionAnswerRepository.findAll().size() == 3
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getAnswerDetails() == null
        and: 'the return value is OK'
        correctAnswers.size() == 0
    }

    def 'conclude quiz with pci question with answer, before conclusionDate'() {
        given: 'a quiz with future conclusionDate'
        pciQuiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = pciQuiz.getId()
        statementQuizDto.quizAnswerId = pciQuizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def correspondences = new ArrayList<Integer>()
        correspondences.add(0)

        def pciLeftOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciLeftOption.getId(), ItemCombinationOption.SIDE.LEFT.name() ,correspondences)
        def pciRightOptionDto = new ItemCombinationOptionStatementAnswerDetailsDto(pciRightOption.getId(), ItemCombinationOption.SIDE.RIGHT.name(), correspondences)

        itemCombinationAnswerDto.getOptions().add(pciLeftOptionDto)
        itemCombinationAnswerDto.getOptions().add(pciRightOptionDto)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(pciQuizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        pciQuizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 3
        def questionAnswer = questionAnswerRepository.findAll().get(1)
        questionAnswer.getQuizAnswer() == pciQuizAnswer
        pciQuizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == pciQuizQuestion
        pciQuizQuestion.getQuestionAnswers().contains(questionAnswer)
        def recLeftPciOption = ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getItemCombinationAnswerOptions().get(0).getItemCombinationOption()
        recLeftPciOption == pciLeftOption

        def recRightPciOption = ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getItemCombinationAnswerOptions().get(1).getItemCombinationOption()
        recRightPciOption == pciRightOption
//        optionOk.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectAnswerDetails().getCorrectOptions().get(0).getCorrespondences().get(0) == pciRightOption.getSequence()
        correctAnswerDto.getCorrectAnswerDetails().getCorrectOptions().get(1).getCorrespondences().get(0) == pciLeftOption.getSequence()

    }

    def 'conclude quiz with pra question with answer, before conclusionDate'() {
        given: 'a quiz with future conclusionDate'
        praQuiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
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

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)
        then: 'the value is createQuestion and persistent'
        praQuizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 3
        def questionAnswer = questionAnswerRepository.findAll().get(2)
        questionAnswer.getQuizAnswer() == praQuizAnswer
        praQuizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == praQuizQuestion
        praQuizQuestion.getQuestionAnswers().contains(questionAnswer)

        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectAnswerDetails().getRegex() == REGEX_1

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}