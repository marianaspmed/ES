import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationOptionStatementQuestionDetails from '@/models/statement/questions/ItemCombinationOptionStatementQuestionDetails';
import { _ } from 'vue-underscore';
import ItemCombinationAnswerOption from '@/models/management/questions/ItemCombinationAnswerOption';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
  options: ItemCombinationOptionStatementQuestionDetails[] = [];

  constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      if (jsonObj.options) {
        this.options = jsonObj.options
          ? jsonObj.options.map(
              (option: ItemCombinationOptionStatementQuestionDetails) =>
                new ItemCombinationOptionStatementQuestionDetails(option)
            )
          : this.options;
      }
    }
  }

  getLeftOptions() {
    const toReturn: ItemCombinationOptionStatementQuestionDetails[] = [];

    this.options.forEach((o) => {
      if (o.side == 'LEFT') {
        toReturn.push(o);
      }
    });
    return toReturn;
  }
  getRightOptions() {
    const toReturn: ItemCombinationOptionStatementQuestionDetails[] = [];

    this.options.forEach((o) => {
      if (o.side == 'RIGHT') {
        toReturn.push(o);
      }
    });
    return toReturn;
  }

  getRightOptionsContent() {
    const toReturn: string[] = [];

    this.options.forEach((o) => {
      if (o.side == 'RIGHT') {
        toReturn.push(o.content);
      }
    });
    return toReturn;
  }
}
