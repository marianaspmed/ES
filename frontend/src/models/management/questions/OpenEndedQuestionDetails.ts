import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionDetails extends QuestionDetails {
  regex: string = '';
  answerField: string = '';

  constructor(jsonObj?: OpenEndedQuestionDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.regex = jsonObj.regex;
      this.answerField = jsonObj.answerField;
    }
  }

  setAsNew(): void {}
}
