export default class ItemCombinationOption {
  id: number | null = null;
  sequence: number = 0;
  side: string = '';
  content: string = '';
  correspondences: number[] = [];

  constructor(jsonObj?: ItemCombinationOption) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.side = jsonObj.side;
      this.content = jsonObj.content;
      this.correspondences = jsonObj.correspondences;
    }
  }

  setAsNew(): void {
    this.id = null;
  }
}
