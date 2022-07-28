import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import PemStatementOption from '@/models/statement/PemStatementOption';
import { _ } from 'vue-underscore';

export default class MultipleChoiceStatementQuestionDetails extends StatementQuestionDetails {
  options: PemStatementOption[] = [];

  constructor(jsonObj?: MultipleChoiceStatementQuestionDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: PemStatementOption) => new PemStatementOption(option)
          )
        );
      }
    }
  }
}
