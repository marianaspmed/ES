import AnswerDetails from '@/models/management/questions/AnswerDetails';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

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

  isCorrect(questionDetails: OpenEndedQuestionDetails): boolean {
    return questionDetails.regex == this.studentAnswer;
  }
  answerRepresentation(): string {
    return this.studentAnswer;
  }

  regexRepresentation(): string {
    return this.regex;
  }
}
