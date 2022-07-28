export default class ItemCombinationOptionStatementAnswerDetails {
  optionId: number | null = null;
  correspondences: number[] = [];

  constructor(jsonObj?: ItemCombinationOptionStatementAnswerDetails) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId || this.optionId;
      this.correspondences = jsonObj.correspondences || this.correspondences;
    }
  }
}
