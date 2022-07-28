import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';

export default class ItemCombinationAnswerOption {
  id: number = 0;
  content: string = '';
  side: string = '';
  sequence: number = 0;

  correspondences: number[] = [];

  constructor(jsonObj?: ItemCombinationAnswerOption) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.side = jsonObj.side;
      this.sequence = jsonObj.sequence;
      this.correspondences = jsonObj.correspondences;
    }
  }

  isCorrect(correctOption: ItemCombinationOption) {
    return (
      this.correspondences.length === correctOption.correspondences.length &&
      this.correspondences.every((v) =>
        correctOption.correspondences.includes(v)
      )
    );
  }
}
