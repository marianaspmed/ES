import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedStatementQuestionDetails extends StatementQuestionDetails {
  regex: string = '';
  answerField: string = '';

  constructor(jsonObj?: OpenEndedStatementQuestionDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.regex = jsonObj.regex;
      this.answerField = jsonObj.answerField;
    }
  }
}
