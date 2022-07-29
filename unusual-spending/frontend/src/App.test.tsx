import React from 'react';
import {mount} from '@cypress/react';
import App from './App';

it('renders a button', () => {
  mount(<App/>);
  cy.get('button').contains('Button text');
});