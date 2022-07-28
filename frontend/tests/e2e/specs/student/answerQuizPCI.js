describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  after(() => {
    cy.clearAllQuestionAnswers();
    cy.clearQuestions();
    cy.clearAllQuizzes();
  });

  afterEach(() => {
    cy.logout();
  });



  it('Student answers a quiz and sees the result', () => {
      cy.createSimplePCIQuestion ('PCI Question Title', 'New PCI Question Content', 'Left Option 1', 'Right Option 1');
      cy.createQuestion('MultipleChoiceQuestion','Multiple Choice Content', 'Option', 'Option', 'Option', 'Correct');
      cy.createQuizzWith2Questions('PCI Quiz','MultipleChoiceQuestion' ,'PCI Question Title');

      //Switch from teacher to student
      cy.logout();
      cy.demoStudentLogin();
      cy.get('[data-cy="quizzesStudentMenuButton"]').click();
      cy.contains('Available').click();

      cy.contains('PCI Quiz').click();


      //Answer the first multiple choice
      cy.get('[data-cy="optionList"]').children().eq(1).click();

      //Go to Next Question
      cy.get('[data-cy="nextQuestionButton"]').click();

      //Answer PCI Question
      cy.get('[data-cy="SelectBox_1"]').parent().click();
      cy.get(".v-list-item__title").contains('Option 1').click();


      //End Quiz
      cy.get('[data-cy="endQuizButton"]').click();
      //Confirm
      cy.get('[data-cy="confirmationButton"]').click();

      //Go to Solved Quizzes
      cy.get('[data-cy="quizzesStudentMenuButton"]').click();
      cy.contains('Solved').click();

      //Click on the solved quiz
      cy.contains('PCI Quiz').click();

      //Check first question
      cy.wait(1500)

      //go to the second question (PCI)
      cy.get('[data-cy="question_number_box_2"]').click();

      //see result
      cy.wait(1500)
  });
});
