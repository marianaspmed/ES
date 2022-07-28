import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

export default class OpenEndedStatementAnswerDetails extends StatementAnswerDetails {
  public regex: string = '';
  public studentAnswer: string = '';

  constructor(jsonObj?: OpenEndedStatementAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.regex = jsonObj.regex;
      this.studentAnswer = jsonObj.studentAnswer;
    }
  }

  isQuestionAnswered(): boolean {
    return this.studentAnswer != '';
  }

  isAnswerCorrect(
    correctAnswerDetails: OpenEndedStatementCorrectAnswerDetails
  ): boolean {
    return (
      !!correctAnswerDetails && this.studentAnswer == correctAnswerDetails.regex
    );
  }
}
