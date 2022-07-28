describe('Manage Item Combination Questions Walk-through', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.route('GET', '/courses/*/questions').as('getQuestions');
    cy.route('GET', '/courses/*/topics').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    cy.wait('@getQuestions').its('status').should('eq', 200);

    cy.wait('@getTopics').its('status').should('eq', 200);
  });

  function selectOnVSelect(
    cydata,
    optionName
  ){
    cy.get(cydata).parent().click();
    cy.get(".v-list-item__title").contains(optionName).click();
  }

  function validateQuestion(
    title,
    content,
    leftOptionContent = 'This is Option 1 (Left)',
    rightOptionContent = 'This is Option 1 (Right)'
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('[data-cy="leftOptions"]').within(($ls) => {
          cy.get('[data-cy="Left_Option"]').each(($el, index, $list) => {
            cy.get($el).should('contain', leftOptionContent);
            cy.get($el).within(($ls) => {
              cy.get('ul').within(($ls) => {
                cy.get('li').each(($el, index, $list) => {
                  cy.get($el).should('contain', 'Option 1');
                });
              })
            });
          });
        });
        cy.get('[data-cy="rightOptions"]').within(($ls) => {
          cy.get('[data-cy="Right_Option"]').each(($el, index, $list) => {
            cy.get($el).should('contain', rightOptionContent);
          });
        });
      });
  }
  function validateQuestionFull(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndex = 2
  ) {
    cy.log('Validate question with show dialog. ' + correctIndex);

    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion(title, content, optionPrefix, correctIndex);

    cy.get('button').contains('close').click();
  }

  afterEach(() => {
    cy.logout();
  });

  it('Creates a new PCI question', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTypeInput"]')
      .type('item_combination', { force: true })
      .click({ force: true });

    cy.get('[data-cy="newLeftOptionButton"]')
      .click();

    cy.get('[data-cy="newRightOptionButton"]')
      .click();

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Item Combination Title', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
      'Item Combination Content',
      {
        force: true,
      }
    );

    cy.wait(1000);

    cy.get('[data-cy="questionOptionsInputLeft_1"]')
      .within(($ls) => {
        cy.get('[data-cy="Option 1"]').type("This is Option 1 (Left)");
      });
    cy.get('[data-cy="questionOptionsInputRight_1"]')
      .within(($ls) => {
        cy.get('[data-cy="Option 1"]').type("This is Option 1 (Right)");
      });

    selectOnVSelect('[data-cy="SelectBox_1"]', 'Option 1');

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

  });

  it('Can view PCI question (with button)', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validateQuestion('Item Combination Title', 'Item Combination Content');

    cy.get('button').contains('close').click();
  });

  it('Can view question (with click)', function () {
    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion('Item Combination Title', 'Item Combination Content');


    cy.get('button').contains('close').click();
  });

  it('Can view question (with button)', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validateQuestion('Item Combination Title', 'Item Combination Content');

    cy.get('button').contains('close').click();
  });
  
  it('Can update content (with button)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('edit').click();
      });

    cy.wait(1000); //making sure codemirror loaded

    selectOnVSelect('[data-cy="SelectBox_1"]', "Option 1");
    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionQuestionTextArea"]')
          .clear({ force: true })
          .type('Cypress New Content For Question!', { force: true });

        cy.get('button').contains('Save').click();
      });


    cy.wait(1000);
    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });
    validateQuestion(
      (title = 'Item Combination Title'),
      (content = 'Cypress New Content For Question!')
    );
  });


  it('Can update Title (with click)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

    cy.wait(1000); //making sure codemirror loaded

    selectOnVSelect('[data-cy="SelectBox_1"]', "Option 1");
    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionTitleTextArea"]')
          .clear({ force: true })
          .type('Cypress New Title For Question!', { force: true });

        cy.get('button').contains('Save').click();
      });


    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });
    validateQuestion(
      (title = 'Cypress New Title For Question!'),
      (content = 'Cypress New Content For Question!')
    );
  });


  it('Can duplicate question', function () {
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
      .should('have.value', 'Cypress New Title For Question!')
      .type('{end} - DUP', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').should(
      'have.value',
      'Cypress New Content For Question!'
    );

    selectOnVSelect('[data-cy="SelectBox_1"]', "Option 1");

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress New Title For Question! - DUP');

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validateQuestion('Cypress New Title For Question! - DUP',
      'Cypress New Content For Question!');

  });

  it('Can delete created question', function () {
    cy.route('DELETE', '/questions/*').as('deleteQuestion');
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('delete').click();
      });

    cy.wait('@deleteQuestion').its('status').should('eq', 200);
  });

});