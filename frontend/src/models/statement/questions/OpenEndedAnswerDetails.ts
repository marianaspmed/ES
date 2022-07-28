import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

export default class OpenEndedAnswerDetails extends AnswerDetails {
  regex: string = '';
  studentAnswer: string = '';

  constructor(jsonObj?: OpenEndedAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.regex = jsonObj.regex;
      this.studentAnswer = jsonObj.studentAnswer;
    }
  }

  isCorrect(): boolean {
    return this.studentAnswer != '' && this.regex == this.studentAnswer;
  }

  answerRepresentation(questionDetails: OpenEndedQuestionDetails): string {
    return this.studentAnswer;
  }
}
