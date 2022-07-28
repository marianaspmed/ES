<template>
  <ul>
    <v-layout>
      <v-layout>
        <v-col xs11>
          <ol data-cy="leftOptions">
            <li
              v-for="option in questionDetails.getLeftOptions()"
              :key="option.id"
              data-cy="Left_Option"
            >
              <span v-html="convertMarkDown(option.content)" />
              <ul v-if="option.correspondences.length > 0">
                <li
                  v-for="corr in option.correspondences"
                  :key="option.id + '_' + corr"
                  data-cy=""
                >
                  Option {{ corr + 1 }}
                </li>
              </ul>
            </li>
          </ol>
        </v-col>
        <v-col offset-md6>
          <ol data-cy="rightOptions">
            <li
              data-cy="Right_Option"
              v-for="option in questionDetails.getRightOptions()"
              :key="option.id"
            >
              <span v-html="convertMarkDown(option.content)" />
            </li>
          </ol>
        </v-col>
      </v-layout>
    </v-layout>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';
import MultipleChoiceAnswerDetails from '@/models/management/questions/MultipleChoiceAnswerDetails';
import ItemCombinationQuestionDetails from '../../models/management/questions/ItemCombinationQuestionDetails';

@Component
export default class ItemCombinationView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;
  //@Prop() readonly answerDetails?: MultipleChoiceAnswerDetails;

  /*studentAnswered(option: number) {
    return this.answerDetails && this.answerDetails?.option.id === option
      ? '**[S]** '
      : '';
  }
  */

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
