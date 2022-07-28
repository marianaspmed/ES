describe('Manage Multiple Choice Questions Walk-through', () => {
  function validatePemQuestion(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndex1 = 2,
    correctIndex2 = 3
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('li').each(($el, index, $list) => {
          cy.get($el).should('contain', optionPrefix + index);
          if (index === correctIndex1 | index === correctIndex2) {
            cy.get($el).should('contain', '[★]');
          } else {
            cy.get($el).should('not.contain', '[★]');
          }
        });
      });
  }

  function validatePemQuestionFull(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndex1 = 2,
    correctIndex2 = 3
  ) {
    cy.log('Validate question with show dialog. ' + correctIndex1 + ', ' + correctIndex2);

    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validatePemQuestion(title, content, optionPrefix, correctIndex1, correctIndex2);

    cy.get('button').contains('close').click();
  }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/courses/*/questions').as('getQuestions');
    cy.route('GET', '/courses/*/topics').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    cy.wait('@getQuestions').its('status').should('eq', 200);

    cy.wait('@getTopics').its('status').should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  // added pem end-to-end tests

  it('Teacher creates a new pem question', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress PEM Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionQuestionTextArea"]'
    ).type('Cypress PEM Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          if (index === 2) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            cy.get(`[data-cy="Relevancy${index + 1}"]`).type(1);
          }
          if (index === 3) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            cy.get(`[data-cy="Relevancy${index + 1}"]`).type(2);
          }
          cy.get(`[data-cy="Option${index + 1}"]`).type('Option ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress PEM Question Example - 01');

    validatePemQuestionFull(
      'Cypress PEM Question Example - 01',
      'Cypress PEM Question Example - Content - 01'
    );
  });

  it('Teacher can view pem question (with button)', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validatePemQuestion(
      'Cypress PEM Question Example - 01',
      'Cypress PEM Question Example - Content - 01'
    );

    cy.get('button').contains('close').click();
  });

  it('Teacher can view pem question (with click)', function () {
    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validatePemQuestion(
      'Cypress PEM Question Example - 01',
      'Cypress PEM Question Example - Content - 01'
    );

    cy.get('button').contains('close').click();
  });

  it('Teacher can update title of pem question (with right-click)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionTitleTextArea"]')
          .clear({ force: true })
          .type('Cypress PEM Question Example - 01 - Edited', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress PEM Question Example - 01 - Edited');

    validatePemQuestionFull(
      (title = 'Cypress PEM Question Example - 01 - Edited'),
      (content = 'Cypress PEM Question Example - Content - 01')
    );
  });

  it('Teacher can update content of pem question (with button)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('edit').click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionQuestionTextArea"]')
          .clear({ force: true })
          .type('Cypress New Content For PEM Question!', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validatePemQuestionFull(
      (title = 'Cypress PEM Question Example - 01 - Edited'),
      (content = 'Cypress New Content For PEM Question!')
    );
  });

  it('Teacher can duplicate pem question', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('cached').click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .should('have.value', 'Cypress PEM Question Example - 01 - Edited')
      .type('{end} - DUP', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').should(
      'have.value',
      'Cypress New Content For PEM Question!'
    );

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          cy.get('textarea').should('have.value', 'Option ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress PEM Question Example - 01 - Edited - DUP');

    validatePemQuestionFull(
      'Cypress PEM Question Example - 01 - Edited - DUP',
      'Cypress New Content For PEM Question!'
    );
  });

  it('Teacher can delete created pem question', function () {
    cy.route('DELETE', '/questions/*').as('deleteQuestion');
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('delete').click();
      });

    cy.wait('@deleteQuestion').its('status').should('eq', 200);
  });




});
