<template>
  <div class="item-combination-create" v-if="this.renderComponent">
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
              <v-text-field
                v-model="option.content"
                :label="`Option ${option.sequence + 1}`"
                :data-cy="`Option ${option.sequence + 1}`"
                rows="1"
                readonly
                auto-grow
              ></v-text-field>
            </v-col>
            <!--            v-model="selectedCorrespondences.get(option).value"-->
            <!--            @change="handleCorrespondenceChange(option)"-->
            <!--            :value="getSelectedRightOption(option)"-->

            <v-col>
              <v-select
                @change="updateValue($event, option)"
                :items="getRightOptionsContent()"
                ref="selectedCorrespondences"
                multiple
                :data-cy="`SelectBox_${option.sequence + 1}`"
                :label="'Correspondences'"
              >
              </v-select>
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
                readonly
                auto-grow
              ></v-textarea>
            </v-col>
          </v-row>
        </v-col>
      </v-layout>
    </v-layout>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import ItemCombinationStatementQuestionDetails from '@/models/statement/questions/ItemCombinationStatementQuestionDetails';
import ItemCombinationStatementAnswerDetails from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationOptionStatementQuestionDetails from '@/models/statement/questions/ItemCombinationOptionStatementQuestionDetails';
import ItemCombinationOptionStatementAnswerDetails from '@/models/statement/questions/ItemCombinationOptionStatementAnswerDetails';

@Component({
  components: {
    BaseCodeEditor,
  },
})
export default class ItemCombinationAnswer extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  answerDetails!: ItemCombinationStatementAnswerDetails;

  renderComponent: boolean = true;

  mounted() {
    this.populateAnswerDetails();
  }

  @Watch('questionDetails')
  onQuestionDetailsChanged() {
    this.answerDetails.clear();
    this.populateAnswerDetails();
    //this.$nextTick(() => {
    // loop over element refs, reset dom value
    // this.$refs.selectedCorrespondences[0].forEach((el: any) => {
    //    el.value = '';
    //  });
    //});
    this.renderComponent = false;
    this.$nextTick(() => {
      this.renderComponent = true;
    });
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getRightOptionsContent() {
    let options: string[] = [];
    this.questionDetails.options.forEach(
      (
        element: ItemCombinationOptionStatementQuestionDetails,
        index: number
      ) => {
        if (element.side == 'RIGHT') {
          options.push('Option ' + (element.sequence + 1));
        }
      }
    );
    return options;
  }

  getCorrsFromStringArray(array: string[]): number[] {
    let corrs = [];
    for (let i = 0; i < array.length; i++) {
      const corr = parseInt(array[i].slice(-1)) - 1;
      corrs.push(corr);
    }
    return corrs;
  }

  getAnswerDetailsFromQuestion(
    option: ItemCombinationOptionStatementQuestionDetails
  ): ItemCombinationOptionStatementAnswerDetails {
    let returnAnswerOpt: ItemCombinationOptionStatementAnswerDetails = new ItemCombinationOptionStatementAnswerDetails();
    let isNew: boolean = true;
    this.answerDetails.options.forEach(
      (answerOpt: ItemCombinationOptionStatementAnswerDetails) => {
        if (answerOpt.optionId == option.id) {
          isNew = false;
          returnAnswerOpt = answerOpt;
        }
      }
    );
    if (isNew) {
      returnAnswerOpt.optionId = option.id;
      this.answerDetails.options.push(returnAnswerOpt);
    }
    return returnAnswerOpt;
  }

  removeItem(itemArray: any[], item: any): any[] {
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

  updateValue(
    array: string[],
    option: ItemCombinationOptionStatementQuestionDetails
  ) {
    const newCorrs = this.getCorrsFromStringArray(array);

    const answerOpt = this.getAnswerDetailsFromQuestion(option);
    const deletedOptions = answerOpt.correspondences.filter(
      (item) => !newCorrs.includes(item)
    );
    answerOpt.correspondences = newCorrs;
    deletedOptions.forEach((deletedIndex: number) => {
      let rightOpt = this.getRightOptions().find(
        (el) => el.sequence == deletedIndex
      );
      if (rightOpt != undefined) {
        let rightAnswerOpt = this.getAnswerDetailsFromQuestion(rightOpt);
        rightAnswerOpt.correspondences = this.removeItem(
          rightAnswerOpt.correspondences,
          option.sequence
        );
      }
    });
    let rightOptions = this.getRightOptions();
    answerOpt.correspondences.forEach((element: number) => {
      const rightOption = rightOptions.find(
        (element2) => element2.sequence == element
      );
      if (rightOption != undefined) {
        this.pushIfDoesntExist(
          this.getAnswerDetailsFromQuestion(rightOption).correspondences,
          option.sequence
        );
      }
    });
    this.$emit('question-answer-update');
  }

  getRightOptions(): ItemCombinationOptionStatementQuestionDetails[] {
    let options: ItemCombinationOptionStatementQuestionDetails[] = [];
    this.questionDetails.options.forEach(
      (
        element: ItemCombinationOptionStatementQuestionDetails,
        index: number
      ) => {
        if (element.side == 'RIGHT') {
          options.push(element);
        }
      }
    );
    return options;
  }

  getLeftOptions(): ItemCombinationOptionStatementQuestionDetails[] {
    let options: ItemCombinationOptionStatementQuestionDetails[] = [];
    this.questionDetails.options.forEach(
      (
        element: ItemCombinationOptionStatementQuestionDetails,
        index: number
      ) => {
        if (element.side == 'LEFT') {
          options.push(element);
        }
      }
    );
    return options;
  }

  populateAnswerDetails() {
    this.questionDetails.getLeftOptions().forEach((leftOption) => {
      let returnAnswerOpt: ItemCombinationOptionStatementAnswerDetails = new ItemCombinationOptionStatementAnswerDetails();
      returnAnswerOpt.optionId = leftOption.id;
      this.answerDetails.options.push(returnAnswerOpt);
    });
  }
}
</script>

<style lang="scss">
.code-order-answer {
  background-color: white;
  display: inline-flex;
  justify-content: space-between;
  align-items: stretch;
  width: 100%;
  position: relative;

  & .code-order-header {
    padding: 10px;
    margin: 0px;

    .question-warning {
      padding: 0;
      margin: 0;
      font-weight: 100;
      font-size: smaller;
    }
  }

  & li {
    padding: 10px;
    margin: 5px 0;
    border: 2px solid rgb(202, 202, 202);
    display: flex;
    justify-content: space-between;
    align-items: center;

    &:not(.dragable) {
      opacity: 0.6;
    }

    & > .content {
      flex-grow: 1;
      max-width: 95%;

      & .CodeMirror {
        height: auto;
      }
    }

    & > .content > *:last-child {
      margin-bottom: 0;
    }
  }

  & > *:not(.question-warning) {
    list-style: none;
    text-align: left;
    flex-grow: 1;
    padding-left: 0 !important;
    width: 50%;
    border: dashed 1px gray;
  }

  & > .code-order-answer-response {
    background-color: rgb(241, 241, 241);

    & > .content {
      flex-grow: 1;
      max-width: 90%;
    }
  }
}
</style>
