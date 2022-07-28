export default class PemStatementOption {
  optionId!: number;
  content!: string;
  relevancy!: number;

  constructor(jsonObj?: PemStatementOption) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.content = jsonObj.content;
      this.relevancy = jsonObj.relevancy;
    }
  }
}
