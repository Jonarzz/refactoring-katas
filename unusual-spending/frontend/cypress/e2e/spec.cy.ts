const LOCAL_STORAGE_KEYS = {
  username: 'username',
};

describe('user payments', () => {

  // TODO react router
  const PAYMENTS_URL = 'http://frontend-app-bucket.s3.localhost.localstack.cloud:4566/index.html';

  // scripts/sql/payment-storage-service.sql should be run on the database before test
  // TODO in the future the data will be added in the tests , so no scripts to load the data exists

  it('displays information about denied access if user is not logged in', () => {
    localStorage.removeItem(LOCAL_STORAGE_KEYS.username);

    cy.visit(PAYMENTS_URL);

    cy.get('div')
      .contains('Access denied');
  });

  it('displays information about no payments if user has no payments', () => {
    localStorage.setItem(LOCAL_STORAGE_KEYS.username, 'user_without_payments');

    cy.visit(PAYMENTS_URL);

    cy.get('div')
      .contains('No payments found');
  });

  it('displays payments table for user with stored payments', () => {
    localStorage.setItem(LOCAL_STORAGE_KEYS.username, 'test_user');

    cy.visit(PAYMENTS_URL);

    const unexpandedBodyCells = [
      '', '399.99', 'USD',
      '',
      '', '12.35', 'PLN',
      ''
    ];
    cy.get('tbody > tr > td')
      .each((cell, idx) => expect(cell).text(unexpandedBodyCells[idx]));
    cy.get('td > button')
      .click({multiple: true});
    const expectedHeaderCells = [
      '', 'Amount', 'Currency',
      'Date', 'Category', 'Description',
      'Date', 'Category', 'Description'
    ];
    cy.get('thead > tr > th')
      .each((cell, idx) => expect(cell).text(expectedHeaderCells[idx]));
    const expandedCells = [
      '10/08/2022 17:29:40', 'travel', 'First payment desc',
      '10/08/2022 17:29:40', 'groceries', 'Second payment desc'
    ];
    cy.get('.payment-row__expandable-box > table > tbody > tr > td')
      .each((cell, idx) => expect(cell).text(expandedCells[idx]));
  });

});
