import {mount} from '@cypress/react';
import React from 'react';
import {PaymentDetailsResponse} from './PaymentRow';
import {PaymentsTable, ResponsePayment, UserPaymentsResponse} from './PaymentsTable';

enum Operation {
  GET_USER_PAYMENTS = 'GetUserPayments',
  GET_PAYMENT_DETAILS = 'GetPaymentDetails'
}

interface Variables {
  readonly username?: string,
  readonly paymentId?: string
}

interface GraphqlStub {
  readonly operationName: Operation,
  readonly variables: Variables,
  readonly queryMatchers: RegExp[],
  readonly responseCode?: number,
  readonly response?: UserPaymentsResponse | PaymentDetailsResponse,
  readonly responseDelayMs?: number
}

const UserPaymentsStubBase: GraphqlStub = {
  operationName: Operation.GET_USER_PAYMENTS,
  queryMatchers: [
    '\\(\\$username: String!',
    'userPayments ?\\(username: \\$username',
  ].map(exp => new RegExp(exp)),
  variables: {}
};
const PaymentDetailsStubBase: GraphqlStub = {
  operationName: Operation.GET_PAYMENT_DETAILS,
  queryMatchers: [
    '\\(\\$paymentId: String!\\)',
    'paymentDetails ?\\(paymentId: \\$paymentId\\)'
  ].map(exp => new RegExp(exp)),
  variables: {}
};

let i = 1;
const createPayment = (amount: number, currency: string, id = ('payment-id-' + i++)): ResponsePayment => ({
  id,
  cost: {
    amount,
    currency: {
      alphaCode: currency,
    },
  },
});

const interceptApiCall = (stubs: GraphqlStub[]) => {
  cy.intercept({
    method: 'POST',
    url: 'http://localhost/api/payment/graphql',
  }, req => {
    const {operationName, variables, query} = req.body;
    stubs.forEach(stub => {
      if (operationName !== stub.operationName) {
        return;
      }
      const queryMismatches = stub.queryMatchers.filter(matcher => !matcher.exec(query));
      if (queryMismatches.length > 0) {
        console.info(`Skipping stubbing as query: ${query} does not match expectations: ${queryMismatches}`);
        return;
      }
      const notMatchingVariables = Object.entries(stub.variables)
                                         .filter(([key, value]) => variables[key] !== value);
      if (notMatchingVariables.length > 0) {
        console.info(`Skipping stubbing as query variables do not match: 
                      expected ${notMatchingVariables} in ${JSON.stringify(variables)}`);
        return;
      }
      req.reply({
        delay: stub.responseDelayMs || 0,
        statusCode: stub.responseCode || 200,
        body: {
          data: stub.response,
        },
      });
    });
  });
};

describe('user payments table', () => {

  it('should display empty table if no result is found', () => {
    const username = 'user-1';
    interceptApiCall([{
      ...UserPaymentsStubBase,
      variables: {username},
      response: {userPayments: []},
    }]);

    mount(<PaymentsTable username={username}/>);

    cy.get('div')
      .contains('No payments found');
  });

  it('should display table header', () => {
    const username = 'user-2';
    interceptApiCall([{
      ...UserPaymentsStubBase,
      variables: {username},
      response: {
        userPayments: [
          createPayment(1.99, 'USD')
        ],
      },
    }]);

    mount(<PaymentsTable username={username}/>);

    const expectedHeaderCells = ['Amount', 'Currency'];
    cy.get('table > thead > tr > th:not([data-test-ignore])')
      .each((cell, idx) => expect(cell).text(expectedHeaderCells[idx]));
  });

  it('should display table with returned results', () => {
    const username = 'user-3',
      firstAmount = 12.35, firstCurrency = 'USD',
      secondAmount = 123.99, secondCurrency = 'PLN';
    interceptApiCall([{
      ...UserPaymentsStubBase,
      variables: {username},
      response: {
        userPayments: [
          createPayment(firstAmount, firstCurrency),
          createPayment(secondAmount, secondCurrency)]
      },
    }]);

    mount(<PaymentsTable username={username}/>);

    const expectedRows = [
      firstAmount, firstCurrency,
      '', // hidden expandable row
      secondAmount, secondCurrency,
      '', // hidden expandable row
    ].map(String);
    cy.get('table > tbody > tr > td:not([data-test-ignore])')
      .each((cell, idx) => expect(cell).text(expectedRows[idx]));
  });

  it('should display a loader while waiting for response', () => {
    const username = 'user-4';
    interceptApiCall([{
      ...UserPaymentsStubBase,
      variables: {username},
      response: {userPayments: []},
    }]);

    mount(<PaymentsTable username={username}/>);

    cy.get('circle')
      .should('be.visible');
  });

  it('should display a user-friendly message if an error occurs', () => {
    const username = 'user-100';
    interceptApiCall([{
      ...UserPaymentsStubBase,
      variables: {username},
      responseCode: 500
    }]);

    mount(<PaymentsTable username={username}/>);

    cy.get('div')
      .contains('Fetching payments failed. Please, contact with the administrator.');
  });

  describe('expandable details row', () => {

    const expandDetailsRow = () => cy.get('td > button')
                                     .click();

    const getProgressBar = () => cy.get('.payment-row__expandable-box > span[role=progressbar]');

    it('should display payment details table header', () => {
      const username = 'user-5', paymentId = '17f7e88d-9e8a-46d3-80ef-fb283d6ef62c';
      interceptApiCall([{
        ...UserPaymentsStubBase,
        variables: {username},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)]
        },
      }, {
        ...PaymentDetailsStubBase,
        variables: {paymentId},
        response: {
          paymentDetails: {
            timestamp: '2022-08-01', description: '', category: { name: '' }
          }
        }
      }]);

      mount(<PaymentsTable username={username}/>);

      expandDetailsRow();
      const expectedHeaderCells = [
        'Date', 'Category', 'Description'
      ];
      cy.get('td table > thead > tr > th')
        .each((cell, idx) => expect(cell).text(expectedHeaderCells[idx]));
    });

    it('should display payment details', () => {
      const username = 'user-6', paymentId = '91f7e88d-9e8a-46d3-80ef-fb283d6ef62c',
        description = 'Card payment at Wallmart', category = 'groceries';
      interceptApiCall([{
        ...UserPaymentsStubBase,
        variables: {username},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)]
        },
      }, {
        ...PaymentDetailsStubBase,
        variables: {paymentId},
        response: {
          paymentDetails: {
            timestamp: '2022-08-01T12:57:23.321+01:00',
            description: description,
            category: {
              name: category
            }
          }
        }
      }]);

      mount(<PaymentsTable username={username}/>);

      expandDetailsRow();
      const expectedCells = [
        '01/08/2022 11:57:23', category, description
      ];
      cy.get('td table > tbody > tr > td')
        .each((cell, idx) => expect(cell).text(expectedCells[idx]));
      getProgressBar()
        .should('not.exist');
    });

    it('should display a loader while waiting for payment details response', () => {
      const username = 'user-7',
        paymentId = '11f7e88d-9e8a-46d3-80ef-fb283d6ef62c';
      interceptApiCall([{
        ...UserPaymentsStubBase,
        variables: {username},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)],
        },
      }, {
        ...PaymentDetailsStubBase,
        variables: {paymentId},
        responseDelayMs: 500,
        response: {
          paymentDetails: {
            timestamp: '2022-08-01', description: '', category: { name: '' }
          }
        }
      }]);

      mount(<PaymentsTable username={username}/>);

      expandDetailsRow();
      getProgressBar()
        .should('be.visible');
    });

    it('should display a user-friendly message if an error occurs', () => {
      const username = 'user-200',
        paymentId = 'payment-with-error';
      interceptApiCall([{
        ...UserPaymentsStubBase,
        variables: {username},
        response: {
          userPayments: [createPayment(1, 'USD', paymentId)],
        },
      }, {
        ...PaymentDetailsStubBase,
        variables: {paymentId},
        responseCode: 500
      }]);

      mount(<PaymentsTable username={username}/>);

      expandDetailsRow();
      cy.get('.payment-row__expandable-box > div[role=alert] > .MuiAlert-message')
        .should('have.text', 'Fetching payment details failed. Please, contact with the administrator.')
        .should('be.visible');
      getProgressBar()
        .should('not.exist');
    });

  });

});
