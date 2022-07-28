<template>
  <div class="item-combination-create">
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
              <v-list>
                <v-list-item
                  v-for="(rightOption, index) in getRightOptions()"
                  :key="index"
                >
                  <v-list-item-content>
                    <v-list-item-title
                      v-if="
                        isAnswered(option, rightOption) &&
                        isOptionCorrespondenceCorrect(option, rightOption)
                      "
                      v-text="rightOption.content + ' ✔'"
                      class="green"
                    >
                    </v-list-item-title>
                    <v-list-item-title
                      v-if="
                        !isAnswered(option, rightOption) &&
                        isOptionCorrespondenceCorrect(option, rightOption)
                      "
                      v-text="rightOption.content + ' ✖'"
                      class="black"
                    >
                    </v-list-item-title>
                    <v-list-item-title
                      v-if="
                        isAnswered(option, rightOption) &&
                        !isOptionCorrespondenceCorrect(option, rightOption)
                      "
                      v-text="rightOption.content + ' ✖'"
                      class="red"
                    >
                    </v-list-item-title>
                    <v-list-item-title
                      v-if="
                        !isAnswered(option, rightOption) &&
                        !isOptionCorrespondenceCorrect(option, rightOption)
                      "
                      v-text="rightOption.content"
                    >
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
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
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import ItemCombinationStatementAnswerDetails from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationStatementQuestionDetails from '@/models/statement/questions/ItemCombinationStatementQuestionDetails';
import ItemCombinationOptionStatementAnswerDetails from '@/models/statement/questions/ItemCombinationOptionStatementAnswerDetails';
import ItemCombinationAnswerOption from '@/models/management/questions/ItemCombinationAnswerOption';
import ItemCombinationOptionStatementQuestionDetails from '@/models/statement/questions/ItemCombinationOptionStatementQuestionDetails';

@Component({
  components: {
    BaseCodeEditor,
  },
})
export default class ItemCombinationAnswerResult extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  readonly answerDetails!: ItemCombinationStatementAnswerDetails;
  @Prop(ItemCombinationStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: ItemCombinationStatementCorrectAnswerDetails;

  isCorrect(
    element: ItemCombinationOptionStatementAnswerDetails,
    index: number
  ) {
    return true;
  }

  isAnswered(
    option: ItemCombinationOptionStatementQuestionDetails,
    rightOption: ItemCombinationOptionStatementQuestionDetails
  ) {
    const answers = this.getAnswerDetailsFromQuestion(option);
    if (answers == null) {
      return false;
    }

    return answers.correspondences.includes(rightOption.sequence);
  }

  isOptionCorrespondenceCorrect(
    leftOption: ItemCombinationOptionStatementQuestionDetails,
    rightOption: ItemCombinationOptionStatementQuestionDetails
  ) {
    const a = this.correctAnswerDetails.correctOptions.find(
      (o) => o.id == leftOption.id
    );
    if (a != undefined) {
      return a.correspondences.includes(rightOption.sequence);
    }
    return false;
  }

  getLeftOptions() {
    return this.questionDetails.getLeftOptions();
  }

  getRightOptions() {
    return this.questionDetails.getRightOptions();
  }

  getRightOptionsContent() {
    return this.correctAnswerDetails.getRightOptions();
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getAnswerDetailsFromQuestion(
    option: ItemCombinationOptionStatementQuestionDetails
  ): ItemCombinationOptionStatementAnswerDetails | null {
    let returnAnswerOpt = null;
    this.answerDetails.options.forEach(
      (answerOpt: ItemCombinationOptionStatementAnswerDetails) => {
        if (answerOpt.optionId == option.id) {
          returnAnswerOpt = answerOpt;
        }
      }
    );
    return returnAnswerOpt;
  }
}
</script>

<style lang="scss">
.red {
  background-color: #cf2323;
  color: white;
}

.green {
  background-color: green;
  color: white;
}

.black {
  background-color: black;
  color: white;
}
</style>
