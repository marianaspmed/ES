export default class Option {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: boolean = false;
  relevancy: number = 0;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
      if (!this.correct) {
        this.relevancy = 0;
      } else {
        this.relevancy = jsonObj.relevancy;
      }
    }
  }
}
