import QuestionDetails from '@/models/management/questions/QuestionDetails';
import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
  options: ItemCombinationOption[] = [];

  constructor(jsonObj?: ItemCombinationQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.options = jsonObj.options
        ? jsonObj.options.map(
            (option: ItemCombinationOption) => new ItemCombinationOption(option)
          )
        : this.options;
    }
  }

  getLeftOptions(): ItemCombinationOption[] {
    return this.getOptionsBySide('LEFT');
  }

  getRightOptions(): ItemCombinationOption[] {
    return this.getOptionsBySide('RIGHT');
  }

  getOptionsBySide(side: String): ItemCombinationOption[] {
    const options: ItemCombinationOption[] = [];

    this.options.forEach((element: ItemCombinationOption) => {
      if (element.side == side) {
        options.push(element);
      }
    });
    return options;
  }

  setAsNew(): void {
    this.options.forEach((option) => {
      option.setAsNew();
    });
  }

  getOptionById(id: number): ItemCombinationOption | null {
    const option = this.options.find((o) => o.id == id);
    if (option == undefined) {
      return null;
    }
    return option;
  }
}
