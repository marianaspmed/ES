<template>
  <span
    v-html="
      convertMarkDown(
        `Regex:  ${questionDetails.regex}
              \nAnswerField: ${questionDetails.answerField}`
      )
    "
  />
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';
import OpenEndedAnswerDetails from '@/models/management/questions/OpenEndedAnswerDetails';
import Image from '@/models/management/Image';

@Component
export default class OpenEndedView extends Vue {
  @Prop() readonly questionDetails!: OpenEndedQuestionDetails;
  @Prop() readonly answerDetails?: OpenEndedAnswerDetails;

  /* studentAnswered(option: number) {
    return this.answerDetails && this.answerDetails?.option.id === option
      ? '**[S]** '
      : '';
  }*/

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
