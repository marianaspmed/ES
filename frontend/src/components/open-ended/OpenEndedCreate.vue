<template>
  <div class="open-ended">
    <v-row>
      <v-col cols="10">
        <v-textarea
          v-model="sQuestionDetails.regex"
          :label="`Regex`"
          :data-cy="`Regex`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col cols="10">
        <v-textarea
          v-model="sQuestionDetails.answerField"
          :label="`answerField`"
          :data-cy="`answerField`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

//TODO erro necessario ter resposta
//Update already works, depends on create
@Component
export default class OpenEndedCreate extends Vue {
  @PropSync('questionDetails', { type: OpenEndedQuestionDetails })
  sQuestionDetails!: OpenEndedQuestionDetails;
}
</script>
