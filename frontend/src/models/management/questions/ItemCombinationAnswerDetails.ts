import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationAnswerOption from '@/models/management/questions/ItemCombinationAnswerOption';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';

export default class ItemCombinationAnswerDetails extends AnswerDetails {
  optionDtos: ItemCombinationAnswerOption[] = [];

  constructor(jsonObj?: ItemCombinationAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.optionDtos = jsonObj.optionDtos.map(
        (option: ItemCombinationAnswerOption) =>
          new ItemCombinationAnswerOption(option)
      );
    }
  }

  isCorrect(correctQuestionDetails: ItemCombinationQuestionDetails): boolean {
    let isCorrect = true;

    this.optionDtos.forEach((currentOption) => {
      const correctOption = correctQuestionDetails.getOptionById(
        currentOption.id
      );
      if (correctOption === null || !currentOption.isCorrect(correctOption)) {
        isCorrect = false;
      }
    });
    return isCorrect;
  }

  answerRepresentation(
    questionDetails: ItemCombinationQuestionDetails
  ): string {
    return (
      this.getLeftOptions()
        .map((x) => '' + (x.sequence || 0) + ' -> ' + x.correspondences)
        .join(' | ') || '-'
    );
  }

  getLeftOptions(): ItemCombinationAnswerOption[] {
    const options: ItemCombinationAnswerOption[] = [];
    this.optionDtos.forEach(
      (element: ItemCombinationAnswerOption, index: number) => {
        if (element.side == 'LEFT') {
          options.push(element);
        }
      }
    );
    return options;
  }
}
