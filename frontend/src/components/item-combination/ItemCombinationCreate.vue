<template>
  <div class="item-combination-create">
    <v-card-actions>
      <v-tooltip top>
        <template v-slot:activator="{ on }">
          <v-btn
            data-cy="newLeftOptionButton"
            color="primary"
            small
            @click="newLeftOption"
            v-on="on"
            >New Left Option</v-btn
          >
        </template>
        <span> Click to add new options to the left. </span>
      </v-tooltip>
      <v-spacer />
      <v-tooltip top>
        <template v-slot:activator="{ on }">
          <v-btn
            data-cy="newRightOptionButton"
            color="primary"
            small
            @click="newRightOption"
            v-on="on"
            >New Right Option</v-btn
          >
        </template>
        <span> Click to add new options to the right. </span>
      </v-tooltip>
    </v-card-actions>
    <v-row>
      <v-layout>
        <v-col cols="1" offset="2"> Left </v-col>
        <v-col cols="1" offset="5"> Right </v-col>
      </v-layout>
    </v-row>
    <v-layout>
      <v-layout>
        <v-col xs11>
          <v-row
            v-for="(option, index) in getLeftOptions()"
            :key="`LEFT_${index}`"
            :data-cy="`questionOptionsInputLeft_${index + 1}`"
          >
            <v-col cols="6">
              <v-textarea
                v-model="option.content"
                :label="`Option ${option.sequence + 1}`"
                :data-cy="`Option ${option.sequence + 1}`"
                rows="1"
                auto-grow
              ></v-textarea>
            </v-col>
            <!--            v-model="selectedCorrespondences.get(option).value"-->
            <!--            @change="handleCorrespondenceChange(option)"-->
            <!--            :value="getSelectedRightOption(option)"-->

            <v-col>
              <v-select
                @change="updateValue($event, option)"
                :items="getRightOptionsContent()"
                multiple
                :data-cy="`SelectBox_${option.sequence + 1}`"
                :label="'Correspondences'"
              >
              </v-select>
            </v-col>
            <v-col v-if="getLeftOptions().length > 1">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    :data-cy="`Delete${index + 1}`"
                    small
                    class="ma-1 action-button"
                    v-on="on"
                    @click="removeOption(option)"
                    color="red"
                    >close</v-icon
                  >
                </template>
                <span>Remove Option</span>
              </v-tooltip>
            </v-col>
          </v-row>
        </v-col>
        <v-col offset-md6>
          <v-row
            v-for="(option, index) in getRightOptions()"
            :key="`RIGHT_${index}`"
            :data-cy="`questionOptionsInputRight_${index + 1}`"
          >
            <v-col cols="10">
              <v-textarea
                v-model="option.content"
                :label="`Option ${option.sequence + 1}`"
                :data-cy="`Option ${option.sequence + 1}`"
                rows="1"
                auto-grow
              ></v-textarea>
            </v-col>
            <v-col v-if="getRightOptions().length > 1">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    :data-cy="`Delete${index + 1}`"
                    small
                    class="ma-1 action-button"
                    v-on="on"
                    @click="removeOption(option)"
                    color="red"
                    >close</v-icon
                  >
                </template>
                <span>Remove Option</span>
              </v-tooltip>
            </v-col>
          </v-row>
        </v-col>
      </v-layout>
    </v-layout>
  </div>
</template>

<script lang="ts">
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import { Component, Vue, Prop, Watch, PropSync } from 'vue-property-decorator';
import draggable from 'vuedraggable';
import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';
@Component({
  components: {
    //CodeOrderSlotEditor,
    draggable,
  },
})
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;
  @Prop({ default: true }) readonly readonlyEdit!: boolean;
  // selectedCorrespondences = new Map();
  // previouslySelectedCorrespondences = new Map();
  created() {
    // minimum options is 1 on each side
    if (this.sQuestionDetails.options.length < 2) {
      let leftOption = new ItemCombinationOption();
      leftOption.sequence = 0;
      leftOption.side = 'LEFT';
      // this.selectedCorrespondences.set(leftOption, []);
      this.sQuestionDetails.options.push(leftOption);
      let rightOption = new ItemCombinationOption();
      rightOption.sequence = 0;
      rightOption.side = 'RIGHT';
      this.sQuestionDetails.options.push(rightOption);
    } else {
      this.sQuestionDetails.options.forEach((option: ItemCombinationOption) => {
        option.correspondences = [];
      });
    }
  }
  mounted() {}
  newLeftOption() {
    this.newOption('LEFT');
  }
  newRightOption() {
    this.newOption('RIGHT');
  }
  newOption(side: string) {
    let newOption = new ItemCombinationOption();
    newOption.side = side;
    newOption.sequence =
      side == 'LEFT'
        ? this.getLeftOptions().length
        : this.getRightOptions().length;
    // if (side == 'LEFT') {
    //   this.selectedCorrespondences.set(newOption, []);
    // }
    this.sQuestionDetails.options.push(newOption);
  }
  updateValue(array: string[], option: ItemCombinationOption) {
    const newCorrs = this.getCorrsFromStringArray(array);
    const deletedOptions: number[] = option.correspondences.filter(
      (item) => !newCorrs.includes(item)
    );
    option.correspondences = newCorrs;
    deletedOptions.forEach((deletedIndex: number) => {
      let rightOpt = this.sQuestionDetails
        .getRightOptions()
        .find((el) => el.sequence == deletedIndex);
      if (rightOpt != undefined) {
        rightOpt.correspondences = this.removeItem(
          rightOpt.correspondences,
          option.sequence
        );
      }
    });
    let rightOptions = this.getRightOptions();
    option.correspondences.forEach((element: number) => {
      const rightOption = rightOptions.find(
        (element2) => element2.sequence == element
      );
      if (rightOption != undefined) {
        this.pushIfDoesntExist(rightOption.correspondences, option.sequence);
      }
    });
  }
  getCorrsFromStringArray(array: string[]): number[] {
    let corrs = [];
    for (let i = 0; i < array.length; i++) {
      const corr = parseInt(array[i].slice(-1)) - 1;
      corrs.push(corr);
    }
    return corrs;
  }
  getLeftOptions(): ItemCombinationOption[] {
    let options: ItemCombinationOption[] = [];
    this.sQuestionDetails.options.forEach(
      (element: ItemCombinationOption, index: number) => {
        if (element.side == 'LEFT') {
          options.push(element);
        }
      }
    );
    return options;
  }
  getRightOptions(): ItemCombinationOption[] {
    let options: ItemCombinationOption[] = [];
    this.sQuestionDetails.options.forEach(
      (element: ItemCombinationOption, index: number) => {
        if (element.side == 'RIGHT') {
          options.push(element);
        }
      }
    );
    return options;
  }
  getSelectedRightOption(option: ItemCombinationOption) {
    let options: string[] = [];
    option.correspondences.forEach((element: number) => {
      options.push('Option ' + (element + 1));
    });
    return options;
  }
  getRightOptionsContent() {
    let options: string[] = [];
    this.sQuestionDetails.options.forEach(
      (element: ItemCombinationOption, index: number) => {
        if (element.side == 'RIGHT') {
          options.push('Option ' + (element.sequence + 1));
        }
      }
    );
    return options;
  }
  // handleCorrespondenceChange(option: ItemCombinationOption) {
  //   let corrs = [];
  //   for (
  //     let i = 0;
  //     i < this.selectedCorrespondences.get(option).value.length;
  //     i++
  //   ) {
  //     const str: string = this.selectedCorrespondences.get(option).value[i];
  //     const corr = parseInt(str.slice(-1)) - 1;
  //     corrs.push(corr);
  //   }
  //
  //   option.correspondences = corrs;
  //
  //   if (this.previouslySelectedCorrespondences.has(option)) {
  //     let deletedOptions: string[] = [];
  //     if (this.previouslySelectedCorrespondences.get(option)[0] != undefined) {
  //       deletedOptions = this.previouslySelectedCorrespondences
  //         .get(option)[0]
  //         .filter(
  //           (e: string) =>
  //             !this.selectedCorrespondences.get(option).value.includes(e)
  //         );
  //     }
  //
  //     if (deletedOptions != undefined && deletedOptions.length !== 0) {
  //       for (let i = 0; i < deletedOptions.length; i++) {
  //         let deletedIndex = parseInt(deletedOptions[i].slice(-1)) - 1;
  //
  //         let rightOpt = this.sQuestionDetails
  //           .getRightOptions()
  //           .find((el) => el.sequence == deletedIndex);
  //
  //         if (rightOpt != undefined) {
  //           rightOpt.correspondences = this.removeItem(
  //             rightOpt.correspondences,
  //             option.sequence
  //           );
  //         }
  //       }
  //     }
  //   }
  //
  //   let rightOptions = this.getRightOptions();
  //
  //   option.correspondences.forEach((element: number) => {
  //     const rightOption = rightOptions.find(
  //       (element2) => element2.sequence == element
  //     );
  //     if (rightOption != undefined) {
  //       this.pushIfDoesntExist(rightOption.correspondences, option.sequence);
  //     }
  //   });
  //
  //   this.previouslySelectedCorrespondences = new Map();
  //
  //   for (let [key, value] of this.selectedCorrespondences) {
  //     this.previouslySelectedCorrespondences.set(key, new Array(value.value));
  //   }
  // }
  removeItem(itemArray: number[], item: number): number[] {
    const index = itemArray.indexOf(item);
    if (index !== -1) {
      itemArray.splice(index, 1);
    }
    return itemArray;
  }
  pushIfDoesntExist(itemArray: number[], item: number) {
    if (!this.checkIfExists(itemArray, item)) {
      itemArray.push(item);
    }
  }
  checkIfExists(itemArray: number[], item: number): boolean {
    return itemArray.some((el) => {
      return el === item;
    });
  }
  removeOption(option: ItemCombinationOption) {
    option.correspondences.forEach((corrSequence: number) => {
      const corrOption = this.sQuestionDetails.options.find(
        (o) => o.sequence == corrSequence && o.side != option.side
      );
      if (corrOption != undefined) {
        this.removeFromArray(corrOption.correspondences, option.sequence);
      }
    });
    this.sQuestionDetails.options.forEach((opt: ItemCombinationOption) => {
      if (opt.sequence > option.sequence && opt.side == option.side) {
        opt.sequence--;
      } else if (opt.side != option.side) {
        for (let i = 0; i < opt.correspondences.length; i++) {
          if (opt.correspondences[i] > option.sequence) {
            opt.correspondences[i]--;
          }
        }
      }
    });
    // for (let key of this.selectedCorrespondences.keys()) {
    //   this.selectedCorrespondences.get(key).value = [];
    //   key.correspondences.forEach((corrSequence: number) => {
    //     this.selectedCorrespondences
    //       .get(key)
    //       .value.push('Option ' + (corrSequence + 1));
    //   });
    // }
    this.removeFromArray(this.sQuestionDetails.options, option);
  }
  removeFromArray(array: any[], item: any) {
    array.splice(array.indexOf(item), 1);
  }
}
</script>

<style lang="scss">
.item-combination-create .CodeMirror {
  height: auto;
}
</style>
