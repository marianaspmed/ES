export default class ItemCombinationOptionStatementQuestionDetails {
  id: number | null = null;
  content: string = '';
  side: string = '';
  sequence: number = 0;

  constructor(jsonObj?: ItemCombinationOptionStatementQuestionDetails) {
    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.content = jsonObj.content || this.content;
      this.side = jsonObj.side || this.side;
      this.sequence = jsonObj.sequence || this.sequence;
    }
  }
}
