import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeOrderStatementCorrectAnswerDetails from '@/models/statement/questions/CodeOrderStatementCorrectAnswerDetails';
import ItemCombinationOptionStatementAnswerDetails from '@/models/statement/questions/ItemCombinationOptionStatementAnswerDetails';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';

export default class ItemCombinationStatementAnswerDetails extends StatementAnswerDetails {
  public options!: ItemCombinationOptionStatementAnswerDetails[];

  constructor(jsonObj?: ItemCombinationStatementAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.options = jsonObj.options || [];
    }
  }

  clear() {
    this.options = [];
  }

  isQuestionAnswered(): boolean {
    return (
      this.options != null &&
      this.options.find((option) => option.correspondences.length > 0) != null
    );
  }

  isAnswerCorrect(
    correctAnswerDetails: ItemCombinationStatementCorrectAnswerDetails
  ): boolean {
    let isCorrect = true;

    //Check if same number of answers
    if (correctAnswerDetails.correctOptions.length != this.options.length) {
      return false;
    }
    correctAnswerDetails.correctOptions.forEach((correctOption) => {
      this.options.forEach((studentAnswerOption) => {
        if (studentAnswerOption.optionId == correctOption.id) {
          if (
            studentAnswerOption.correspondences.length !=
              correctOption.correspondences.length ||
            !(
              studentAnswerOption.correspondences.length ===
                correctOption.correspondences.length &&
              studentAnswerOption.correspondences.every((v) =>
                correctOption.correspondences.includes(v)
              )
            )
          ) {
            isCorrect = false;
          }
        }
      });
    });
    return isCorrect;
  }
}
