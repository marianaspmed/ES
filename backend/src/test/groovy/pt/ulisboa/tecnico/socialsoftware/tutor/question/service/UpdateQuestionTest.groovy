package pt.ulisboa.tecnico.socialsoftware.tutor.question.service
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
@DataJpaTest
class UpdateQuestionTest extends SpockTest {
    def question
    def pemQuestion
    def optionCorrect_1
    def optionCorrect_2
    def optionNonCorrect_3
    def pciQuestion
    def praQuestion
    def optionOK
    def optionKO
    def user
    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)
        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setImage(image)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and: 'a pciQuestion'
        pciQuestion = new Question()
        pciQuestion.setCourse(externalCourse)
        pciQuestion.setKey(1)
        pciQuestion.setTitle(QUESTION_1_TITLE)
        pciQuestion.setContent(QUESTION_1_CONTENT)
        pciQuestion.setStatus(Question.Status.AVAILABLE)
        def questionDetails2 = new ItemCombinationQuestion()
        pciQuestion.setQuestionDetails(questionDetails2)
        questionDetailsRepository.save(questionDetails2)
        questionRepository.save(pciQuestion)
        and: 'a praQuestion'
        praQuestion = new Question()
        praQuestion.setCourse(externalCourse)
        praQuestion.setKey(1)
        praQuestion.setTitle(QUESTION_5_TITLE)
        praQuestion.setContent(QUESTION_5_CONTENT)
        praQuestion.setStatus(Question.Status.AVAILABLE)
        def questionDetails3 = new OpenEndedQuestion()
        praQuestion.setQuestionDetails(questionDetails3)
        questionDetails3.setRegex(REGEX_1)
        questionDetails3.setAnswerField(ANSWER_1)
        questionDetailsRepository.save(questionDetails3)
        questionRepository.save(praQuestion)
        and: 'a pemQuestion'
        pemQuestion = new Question()
        pemQuestion.setCourse(externalCourse)
        pemQuestion.setKey(1)
        pemQuestion.setTitle(QUESTION_1_TITLE)
        pemQuestion.setContent(QUESTION_1_CONTENT)
        pemQuestion.setStatus(Question.Status.AVAILABLE)
        def questionDetails4 = new MultipleChoiceQuestion()
        pemQuestion.setQuestionDetails(questionDetails4)
        questionDetailsRepository.save(questionDetails4)
        questionRepository.save(pemQuestion)
        and: 'three options'
        optionCorrect_1 = new Option()
        optionCorrect_1.setContent(OPTION_1_CONTENT)
        optionCorrect_1.setCorrect(true)
        optionCorrect_1.setRelevancy(1)
        optionCorrect_1.setQuestionDetails(questionDetails4)
        optionRepository.save(optionCorrect_1)
        optionCorrect_2 = new Option()
        optionCorrect_2.setContent(OPTION_2_CONTENT)
        optionCorrect_2.setCorrect(true)
        optionCorrect_2.setRelevancy(2)
        optionCorrect_2.setQuestionDetails(questionDetails4)
        optionRepository.save(optionCorrect_2)
        optionNonCorrect_3 = new Option()
        optionNonCorrect_3.setContent(OPTION_3_CONTENT)
        optionNonCorrect_3.setCorrect(false)
        optionNonCorrect_3.setQuestionDetails(questionDetails4)
        optionRepository.save(optionNonCorrect_3)
    }
    def "update a question"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)
        then: "the question is changed"
        questionRepository.count() == 4L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.isCorrect()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_2_CONTENT
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> !option.isCorrect()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        and: 'there are two questions in the database'
        optionRepository.findAll().size() == 5
    }
    def "update question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle('     ')
        when:
        questionService.updateQuestion(question.getId(), questionDto)
        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }
    def "update question with two options true"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        when:
        questionService.updateQuestion(question.getId(), questionDto)
        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        // since now its possible to have 2 correct options, is this test valid or can it be changed?
        //exception.getErrorMessage() == ErrorMessage.ONE_CORRECT_OPTION_NEEDED
        exception.getErrorMessage() == ErrorMessage.MUST_ASSIGN_RELEVANCY
    }
    def "update correct option in a question with answers"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)
        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        when:
        questionService.updateQuestion(question.getId(), questionDto)
        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CHANGE_ANSWERED_QUESTION
    }
    def "update a question with a pci with no items"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        when:
        questionService.updateQuestion(pciQuestion.getId(), questionDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEM_SETS_NEEDED
    }
    def "update a question with a pci with no item correspondences"(){
        given: "a questionDto"
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
        def options = new ArrayList<ItemCombinationOptionDto>()
        options.add(leftOptionDto)
        rightOptionDto.setContent(OPTION_2_CONTENT)
        rightOptionDto.setSequence(0)
        rightOptionDto.setSide(ItemCombinationOption.SIDE.RIGHT.name())
        options.add(rightOptionDto)
        def questionDetailsDto= new ItemCombinationQuestionDto()
        questionDetailsDto.setOptions(options)
        questionDto.setQuestionDetailsDto(questionDetailsDto)
        when:
        questionService.updateQuestion(pciQuestion.getId(), questionDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_CORRESPONDENCE_NEEDED
    }
    def "update a question with a pci with no items on the left"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        def rightOptionDto = new ItemCombinationOptionDto()
        rightOptionDto.setContent(OPTION_2_CONTENT)
        rightOptionDto.setSequence(0)
        rightOptionDto.setSide(ItemCombinationOption.SIDE.RIGHT.name())
        def options = new ArrayList<ItemCombinationOptionDto>()
        options.add(rightOptionDto)
        def questionDetailsDto = new ItemCombinationQuestionDto()
        questionDetailsDto.setOptions(options)
        questionDto.setQuestionDetailsDto(questionDetailsDto)
        when:
        questionService.updateQuestion(pciQuestion.getId(), questionDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEM_SETS_NEEDED
    }
    def "update a question with a pci with no items on the right"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        def leftOptionDto = new ItemCombinationOptionDto()
        leftOptionDto.setContent(OPTION_1_CONTENT)
        leftOptionDto.setSequence(0)
        leftOptionDto.setSide(ItemCombinationOption.SIDE.LEFT.name())
        def options = new ArrayList<ItemCombinationOptionDto>()
        options.add(leftOptionDto)
        def questionDetailsDto = new ItemCombinationQuestionDto()
        questionDetailsDto.setOptions(options)
        questionDto.setQuestionDetailsDto(questionDetailsDto)
        when:
        questionService.updateQuestion(pciQuestion.getId(), questionDto)
        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEM_SETS_NEEDED
    }
    def "update a question with a valid pci"(){
        given: "a pci question and a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'two options'
        def leftOptionDto = new ItemCombinationOptionDto()
        def rightOptionDto = new ItemCombinationOptionDto()
        leftOptionDto.setContent(OPTION_1_CONTENT)
        leftOptionDto.setSequence(0)
        leftOptionDto.setSide(ItemCombinationOption.SIDE.LEFT.name())
        def options = new ArrayList<ItemCombinationOptionDto>()
        options.add(leftOptionDto)
        rightOptionDto.setContent(OPTION_2_CONTENT)
        rightOptionDto.setSequence(0)
        rightOptionDto.setSide(ItemCombinationOption.SIDE.RIGHT.name())
        leftOptionDto.getCorrespondences().add(rightOptionDto.getSequence())
        rightOptionDto.getCorrespondences().add(leftOptionDto.getSequence())
        options.add(rightOptionDto)
        def questionDetailsDto = new ItemCombinationQuestionDto()
        questionDetailsDto.setOptions(options)
        questionDto.setQuestionDetailsDto(questionDetailsDto)
        when:
        questionService.updateQuestion(pciQuestion.getId(), questionDto)
        then: "the correct question is inside the repository"
        questionRepository.count() == 4L
        def result = questionRepository.findAll().get(1)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getOptions().size() == 2
        def left_option = result.getQuestionDetails().getOptions().get(0)
        def right_option = result.getQuestionDetails().getOptions().get(1)
        left_option.getContent() == OPTION_1_CONTENT
        left_option.getCorrespondences().toArray()[0] == right_option
        left_option.getSide() == ItemCombinationOption.SIDE.LEFT
        right_option.getContent() == OPTION_2_CONTENT
        right_option.getCorrespondences().toArray()[0] == left_option
        right_option.getSide() == ItemCombinationOption.SIDE.RIGHT
    }

    def "update a pra question with answerField and regex"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def questionDetailsDto = new OpenEndedQuestionDto()
        questionDetailsDto.setAnswerField(ANSWER_1)
        questionDetailsDto.setRegex(REGEX_1)

        questionDto.setQuestionDetailsDto(questionDetailsDto)

        when:
        questionService.updateQuestion(praQuestion.getId(), questionDto)

        then: "the question is changed"
        def result = questionRepository.findAll().get(2)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        def resAnswerField = result.getQuestionDetails().getAnswerField()
        def resRegex = result.getQuestionDetails().getRegex()
        resAnswerField == ANSWER_1
        resRegex == REGEX_1

    }

    def "cannot update a pra question with null answerField"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def questionDetailsDto = new OpenEndedQuestionDto()
        questionDetailsDto.setAnswerField(null)

        questionDto.setQuestionDetailsDto(questionDetailsDto)

        when:
        questionService.updateQuestion(praQuestion.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ANSWER_IS_NULL
    }

    def "cannot update a pra question with empty answerField"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def questionDetailsDto = new OpenEndedQuestionDto()
        questionDetailsDto.setAnswerField("")

        questionDto.setQuestionDetailsDto(questionDetailsDto)

        when:
        questionService.updateQuestion(praQuestion.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ANSWER_IS_NULL
    }


    //added pem tests

    def "update the correct options of a pem and its relevancy"() {
        // to add remove options
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "three options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionCorrect_1)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        optionDto = new OptionDto(optionCorrect_2)
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        optionDto = new OptionDto(optionNonCorrect_3)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(2)
        options.add(optionDto)

        when:
        questionService.updateQuestion(pemQuestion.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 4L
        def result = questionRepository.findAll().get(3)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        and: 'the options are changed'
        result.getQuestionDetails().getOptions().size() == 3
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getSequence() == 0 }).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_2_CONTENT
        !resOptionOne.isCorrect()
        resOptionOne.getRelevancy() == 0

        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getSequence() == 1 }).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_3_CONTENT
        resOptionTwo.isCorrect()
        resOptionTwo.getRelevancy() == 1

        def resOptionThree = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getSequence() == 2 }).findAny().orElse(null)
        resOptionThree.getContent() == OPTION_1_CONTENT
        resOptionThree.isCorrect()
        resOptionThree.getRelevancy() == 2
    }

    def "cannot update a pem with two or more correct options with the same relevancy"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "three options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionCorrect_1)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        options.add(optionDto)

        optionDto = new OptionDto(optionCorrect_2)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        options.add(optionDto)

        optionDto = new OptionDto(optionNonCorrect_3)
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(pemQuestion.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DUPLICATED_RELEVANCY
    }

    /*def "cannot update pem by assigning relevancy to non correct options of a pem"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "three options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionCorrect_1)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(2)
        options.add(optionDto)

        optionDto = new OptionDto(optionNonCorrect_3)
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(false)
        optionDto.setRelevancy(1)
        options.add(optionDto)

        optionDto = new OptionDto(optionCorrect_2)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(pemQuestion.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_RELEVANCY_ASSIGNMENT
    }*/

    def "cannot update a pem adding invalid relevancy order to correct options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "three options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionCorrect_1)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(1)
        options.add(optionDto)

        optionDto = new OptionDto(optionCorrect_2)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevancy(4)
        options.add(optionDto)

        optionDto = new OptionDto(optionNonCorrect_3)
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(pemQuestion.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_RELEVANCY_ORDER
    }

    def "update MultipleChoiceQuestion remove old option add new one"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        questionDto.setQuestionDetailsDto(multipleChoiceQuestionDto)
        and: 'a the old correct option'
        def newOptionOK = new OptionDto(optionOK)
        and: 'a new option'
        def newOptionKO = new OptionDto()
        newOptionKO.setContent(OPTION_1_CONTENT)
        newOptionKO.setCorrect(false)
        and: 'add options to dto'
        def newOptions = new ArrayList<OptionDto>()
        newOptions.add(newOptionOK)
        newOptions.add(newOptionKO)
        multipleChoiceQuestionDto.setOptions(newOptions)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is there"
        questionRepository.count() == 4L
        def result = questionRepository.findAll().get(0)
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.isCorrect()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_1_CONTENT
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> !option.isCorrect()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        and: 'there are two questions in the database'
        optionRepository.findAll().size() == 5
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}