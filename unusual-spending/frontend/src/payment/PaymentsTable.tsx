import {ApolloQueryResult, gql} from '@apollo/client';
import {Alert, Button, CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';
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
query GetUserPayments($userId: BigInteger!) {
  userPayments (userId: $userId) {
    cost {
      id
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

const PaymentsTable = ({userId}: {userId: number}) => {

  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>();

  const getUserPayments = (userId: number, force = false) => {
    setLoading(true);
    // TODO pagination
    runQuery(USER_PAYMENTS_QUERY, {userId}, force)
      .then(result => {
        setPayments(mapPayments(result));
        setError(undefined);
      })
      .catch(error => {
        console.error(error.message);
        setError(error.message);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    getUserPayments(userId);
  }, []);

  if (loading) {
    return <CircularProgress/>;
  }

  if (error) {
    return (
      <Alert severity="error" variant="outlined">
        Fetching payments failed. Please, contact with the administrator.
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
      <Button onClick={() => getUserPayments(userId, true)}>Reload</Button>
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
            {payments.map((payment) => <PaymentRow payment={payment}
                                                   key={payment.id}/>)}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

PaymentsTable.propTypes = {
  userId: PropTypes.number.isRequired
};

export default PaymentsTable;
