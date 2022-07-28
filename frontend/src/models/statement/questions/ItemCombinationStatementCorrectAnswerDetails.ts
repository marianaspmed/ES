import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationAnswerOption from '@/models/management/questions/ItemCombinationAnswerOption';

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptions!: ItemCombinationAnswerOption[];

  constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.correctOptions = jsonObj.correctOptions || [];
    }
  }

  getLeftOptions() {
    const toReturn: ItemCombinationAnswerOption[] = [];

    this.correctOptions.forEach((o) => {
      if (o.side == 'LEFT') {
        toReturn.push(o);
      }
    });
    return toReturn;
  }

  getRightOptions() {
    const toReturn: ItemCombinationAnswerOption[] = [];

    this.correctOptions.forEach((o) => {
      if (o.side == 'RIGHT') {
        toReturn.push(o);
      }
    });
    return toReturn;
  }

  getRightOptionsContent() {
    const toReturn: string[] = [];

    this.correctOptions.forEach((o) => {
      if (o.side == 'RIGHT') {
        toReturn.push(o.content);
      }
    });
    return toReturn;
  }
}
