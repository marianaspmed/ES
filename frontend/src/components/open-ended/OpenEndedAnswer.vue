<template>
  <div class="open-ended-answer">
    <v-row>
      <v-col cols="10">
        <v-textarea
          v-model.lazy="answerDetails.studentAnswer"
          :label="`Answer`"
          :data-cy="`studentAnswer`"
          rows="5"
          auto-grow
        ></v-textarea>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import OpenEndedStatementQuestionDetails from '@/models/statement/questions/OpenEndedStatementQuestionDetails';
import Image from '@/models/management/Image';
import OpenEndedStatementAnswerDetails from '@/models/statement/questions/OpenEndedStatementAnswerDetails';
import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

@Component
export default class OpenEndedAnswer extends Vue {
  @Prop(OpenEndedStatementQuestionDetails)
  readonly questionDetails!: OpenEndedStatementQuestionDetails;
  @Prop(OpenEndedStatementAnswerDetails)
  answerDetails!: OpenEndedStatementAnswerDetails;
  @Prop(OpenEndedStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: OpenEndedStatementCorrectAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped>
.unanswered {
  .correct {
    .open-ended-answer {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
