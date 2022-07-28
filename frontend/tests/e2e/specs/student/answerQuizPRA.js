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
        cy.createSimplePRAQuestion ('PRA Question Title', 'New PRA Question Content', 'answerField', 'regex');
        cy.createQuestion('MultipleChoiceQuestion','Multiple Choice Content', 'Option', 'Option', 'Option', 'Correct');
        cy.createQuizzWith2Questions('PRA Quiz','MultipleChoiceQuestion' ,'PRA Question Title');

        //Switch from teacher to student
        cy.logout();
        cy.demoStudentLogin();
        cy.get('[data-cy="quizzesStudentMenuButton"]').click();
        cy.contains('Available').click();

        cy.contains('PRA Quiz').click();


        //Answer the first multiple choice
        cy.get('[data-cy="optionList"]').children().eq(1).click();

        //Go to Next Question
        cy.get('[data-cy="nextQuestionButton"]').click();

        //Answer PRA Question

        cy.get(
            '[data-cy="studentAnswer"]'
        ).type('regex', { force: true });


        //End Quiz
        cy.get('[data-cy="endQuizButton"]').click();
        //Confirm
        cy.get('[data-cy="confirmationButton"]').click();

        //Go to Solved Quizzes
        cy.get('[data-cy="quizzesStudentMenuButton"]').click();
        cy.contains('Solved').click();

        //Click on the solved quiz
        cy.contains('PRA Quiz').click();

        //Check first question
        cy.wait(1500)

        //go to the second question (PRA)
        cy.get('[data-cy="question_number_box_2"]').click();

        //see result
        cy.wait(1500)
    });
});