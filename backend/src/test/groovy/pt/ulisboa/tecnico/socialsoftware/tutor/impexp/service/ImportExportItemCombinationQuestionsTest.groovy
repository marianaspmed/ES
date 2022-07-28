package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportItemCombinationQuestionsTest extends SpockTest{
    def questionId

    def setup() {
        createExternalCourseAndExecution()

        def questionDto = new QuestionDto()
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

        def question = new ItemCombinationQuestionDto()
        question.setOptions(options)

        questionDto.setQuestionDetailsDto(question)
        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()
    }

    def 'export question to Latex'() {
        given: 'a question'
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        def returnString = '\\newcommand{\\q' + 'Question1Title' + 'B' + '}{\n' + '\\begin{ClosedQuestion}\n\t' + QUESTION_1_CONTENT + '\n\n' + '\\begin{lstlisting}\n' + OPTION_1_CONTENT + '\n\\end{lstlisting}\n' + '\\begin{lstlisting}\n' + OPTION_2_CONTENT + '\n\\end{lstlisting}\n' + '% Answer: \n\\begin{lstlisting}\n' + '0 -> 0\n' + '\\end{lstlisting}\n' + '\\end{ClosedQuestion}\n}\n\n'
        questionsLatex.equals(returnString)

    }

    def 'export and import PCI questions to xml'() {
        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: 'a clean database'
        questionService.removeQuestion(questionId)
        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionRepository.findAll().get(0)
        questionResult.getKey() == 1
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE
        questionResult.getQuestionDetails().getOptions().size() == 2
        def leftOption = questionResult.getQuestionDetails().getOptions().get(0)
        def rightOption = questionResult.getQuestionDetails().getOptions().get(1)

        leftOption.getSequence() == 0
        leftOption.getContent() == OPTION_1_CONTENT
        leftOption.getCorrespondences().get(0) == rightOption


        rightOption.getSequence() == 0
        rightOption.getContent() == OPTION_2_CONTENT
        rightOption.getCorrespondences().get(0) == leftOption
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
