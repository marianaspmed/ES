import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public regex: string = '';
  public studentAnswer: string = '';

  constructor(jsonObj?: OpenEndedStatementCorrectAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.regex = jsonObj.regex;
      this.studentAnswer = jsonObj.studentAnswer;
    }
  }
}
