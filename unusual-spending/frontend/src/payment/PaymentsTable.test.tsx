import {mount} from '@cypress/react';
import React from 'react';
import {PaymentDetailsResponse} from './PaymentRow';
import PaymentsTable, {ResponsePayment, UserPaymentsResponse} from './PaymentsTable';

enum Operation {
  GET_USER_PAYMENTS = 'GetUserPayments',
  GET_PAYMENT_DETAILS = 'GetPaymentDetails'
}

interface Variables {
  readonly userId?: number,
  readonly paymentId?: string
}

interface GraphqlStub {
  readonly operationName: Operation.GET_USER_PAYMENTS | Operation.GET_PAYMENT_DETAILS,
  readonly variables: Variables,
  readonly response: UserPaymentsResponse | PaymentDetailsResponse,
  readonly responseDelayMs?: number
}

const createPayment = (amount: number, currency: string, id = 'payment-id'): ResponsePayment => ({
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
    stubs.forEach(stub => {
      const {operationName, variables} = req.body;
      if (operationName === stub.operationName) {
        const notMatchingVariables = Object.entries(stub.variables)
                                           .filter(([key, value]) => variables[key] !== value);
        if (notMatchingVariables.length > 0) {
          throw `Query variables mismatch - expected ${notMatchingVariables} in ${JSON.stringify(variables)}`;
        }
        req.reply({
          delay: stub.responseDelayMs || 0,
          body: {
            data: stub.response,
          },
        });
      }
    });
  });
};

describe('user payments table', () => {

  it('should display empty table if no result is found', () => {
    const userId = 1;
    interceptApiCall([{
      operationName: Operation.GET_USER_PAYMENTS,
      variables: {userId},
      response: {userPayments: []},
    }]);

    mount(<PaymentsTable userId={userId}/>);

    cy.get('div')
      .contains('No payments found');
  });

  it('should display table header', () => {
    const userId = 2;
    interceptApiCall([{
      operationName: Operation.GET_USER_PAYMENTS,
      variables: {userId},
      response: {
        userPayments: [
          createPayment(1.99, 'USD')
        ],
      },
    }]);

    mount(<PaymentsTable userId={userId}/>);

    const expectedHeaderCells = ['Amount', 'Currency'];
    cy.get('table > thead > tr > th:not([data-test-ignore])')
      .each((cell, idx) => cy.wrap(cell).should('have.text', expectedHeaderCells[idx]));
  });

  it('should display table with returned results', () => {
    const userId = 3,
      firstAmount = 12.35, firstCurrency = 'USD',
      secondAmount = 123.99, secondCurrency = 'PLN';
    interceptApiCall([{
      operationName: Operation.GET_USER_PAYMENTS,
      variables: {userId},
      response: {
        userPayments: [
          createPayment(firstAmount, firstCurrency),
          createPayment(secondAmount, secondCurrency)]
      },
    }]);

    mount(<PaymentsTable userId={userId}/>);

    const expectedRows = [
      firstAmount, firstCurrency,
      '', // hidden expandable row
      secondAmount, secondCurrency,
      '', // hidden expandable row
    ];
    cy.get('table > tbody > tr > td:not([data-test-ignore])')
      .each((cell, idx) => cy.wrap(cell).should('have.text', expectedRows[idx]));
  });

  it('should display a loader while waiting for response', () => {
    const userId = 4;
    interceptApiCall([{
      operationName: Operation.GET_USER_PAYMENTS,
      variables: {userId},
      response: {userPayments: []},
    }]);

    mount(<PaymentsTable userId={userId}/>);

    cy.get('circle')
      .should('be.visible');
  });

  describe('expanded row', () => {

    it('should display payment details table header', () => {
      const userId = 5, paymentId = '17f7e88d-9e8a-46d3-80ef-fb283d6ef62c';
      interceptApiCall([{
        operationName: Operation.GET_USER_PAYMENTS,
        variables: {userId},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)]
        },
      }, {
        operationName: Operation.GET_PAYMENT_DETAILS,
        variables: {paymentId},
        response: {
          paymentDetails: {
            timestamp: '2022-08-01', description: '', category: { name: '' }
          }
        }
      }]);

      mount(<PaymentsTable userId={userId}/>);

      cy.get('td > button')
        .click();
      const expectedHeaderCells = [
        'Date', 'Category', 'Description'
      ];
      cy.get('td table > thead > tr > th')
        .each((cell, idx) => cy.wrap(cell)
                               .should('have.text', expectedHeaderCells[idx])
                               .should('be.visible'));
    });

    it('should display payment details', () => {
      const userId = 6, paymentId = '91f7e88d-9e8a-46d3-80ef-fb283d6ef62c',
        description = 'Card payment at Wallmart', category = 'groceries';
      interceptApiCall([{
        operationName: Operation.GET_USER_PAYMENTS,
        variables: {userId},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)]
        },
      }, {
        operationName: Operation.GET_PAYMENT_DETAILS,
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

      mount(<PaymentsTable userId={userId}/>);

      cy.get('td > button')
        .click();
      const expectedCells = [
        '01/08/2022 11:57:23', category, description
      ];
      cy.get('td table > tbody > tr > td')
        .each((cell, idx) => cy.wrap(cell)
                               .should('have.text', expectedCells[idx])
                               .should('be.visible'));
    });

    it('should display a loader while waiting for payment details response', () => {
      const userId = 7,
        paymentId = '11f7e88d-9e8a-46d3-80ef-fb283d6ef62c';
      interceptApiCall([{
        operationName: Operation.GET_USER_PAYMENTS,
        variables: {userId},
        response: {
          userPayments: [createPayment(9.99, 'USD', paymentId)],
        },
      }, {
        operationName: Operation.GET_PAYMENT_DETAILS,
        variables: {paymentId},
        responseDelayMs: 500,
        response: {
          paymentDetails: {
            timestamp: '2022-08-01', description: '', category: { name: '' }
          }
        }
      }]);

      mount(<PaymentsTable userId={userId}/>);

      cy.get('td > button')
        .click();
      cy.get('span[role=progressbar]')
        .should('be.visible');
    });

  });

});