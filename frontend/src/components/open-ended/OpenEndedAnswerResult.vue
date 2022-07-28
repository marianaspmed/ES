<template>
  <div>
    <v-row>
      <v-col cols="10">
        <v-textarea
          class="open-ended-student-answer"
          v-model="answerDetails.studentAnswer"
          :label="`Student's Answer`"
          :data-cy="`studentAnswer`"
          :readonly="true"
          rows="2"
          auto-grow
        ></v-textarea>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="10">
        <v-textarea
          class="open-ended-teacher-answer"
          v-model="questionDetails.answerField"
          :label="`Teacher's Answer`"
          :data-cy="`teacherAnswer`"
          :readonly="true"
          rows="2"
          auto-grow
        ></v-textarea>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="10">
        <v-textarea
          class="open-ended-teacher-regex"
          v-model="questionDetails.regex"
          :label="`Teacher's Regex`"
          :data-cy="`teacherRegex`"
          :readonly="true"
          rows="2"
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
export default class OpenEndedAnswerResult extends Vue {
  @Prop(OpenEndedStatementQuestionDetails)
  readonly questionDetails!: OpenEndedStatementQuestionDetails;
  @Prop(OpenEndedStatementAnswerDetails)
  answerDetails!: OpenEndedStatementAnswerDetails;
  @Prop(OpenEndedStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: OpenEndedStatementCorrectAnswerDetails;

  isQuestionAnswered(): boolean {
    return this.answerDetails.studentAnswer != '';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped></style>
