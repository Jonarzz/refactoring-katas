import {ApolloQueryResult, gql} from '@apollo/client';
import {Alert, Button, CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import React, {useEffect, useRef, useState} from 'react';
import {runQuery} from './PaymentApiClient';
import PaymentRow from './PaymentRow';
import './PaymentsTable.css';

export interface ResponsePayment {
  readonly id: string,
  readonly cost: {
    readonly amount: number,
    readonly currency: {
      readonly alphaCode: string
    }
  }
}

export interface UserPaymentsResponse {
  readonly userPayments: Array<ResponsePayment>
}

export interface Payment {
  readonly id: string,
  readonly amount: number,
  readonly currency: string
}

const USER_PAYMENTS_QUERY = gql`
query GetUserPayments($username: String!) {
  userPayments (username: $username) {
    id
    cost {
      amount
      currency {
        alphaCode
      }
    }
  }
}
`;

const mapPayments = (result: ApolloQueryResult<UserPaymentsResponse>): Payment[] =>
  result.data.userPayments.map(payment => {
    const {amount, currency} = payment.cost;
    return ({
      id: payment.id,
      amount,
      currency: currency.alphaCode,
    });
  });

export const PaymentsTable = () => {

  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>();
  const username = useRef('');

  const getUserPayments = (username: string, force = false) => {
    setLoading(true);
    // TODO add dates to the query
    // TODO pagination
    runQuery(USER_PAYMENTS_QUERY, {username}, force)
      .then(result => {
        setPayments(mapPayments(result));
        setError(undefined);
        setLoading(false)
      })
      .catch(error => {
        setError('Fetching payments failed. Please, contact with the administrator.');
        setLoading(false);
        console.error(error);
      });
  };

  useEffect(() => {
    const loggedInUser = localStorage.getItem('username');
    if (!loggedInUser) {
      // TODO redirect to login page
      setError('Access denied');
      setLoading(false);
      return;
    }
    username.current = loggedInUser;
    getUserPayments(loggedInUser);
  }, []);

  if (loading) {
    return <CircularProgress/>;
  }

  if (error) {
    return (
      <Alert severity="error" variant="outlined">
        {error}
      </Alert>
    );
  }

  if (payments.length === 0) {
    return (
      <Alert severity="info" variant="outlined">
        No payments found
      </Alert>
    );
  }

  return (
    <div>
      <Button onClick={() => getUserPayments(username.current, true)}>Reload</Button>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell data-test-ignore/>
              <TableCell>Amount</TableCell>
              <TableCell>Currency</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payments.map(payment => <PaymentRow payment={payment}
                                                 key={payment.id}/>)}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};
