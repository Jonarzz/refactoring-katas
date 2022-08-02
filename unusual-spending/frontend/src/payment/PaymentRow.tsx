import {ApolloQueryResult, gql} from '@apollo/client';
import {KeyboardArrowDown, KeyboardArrowUp} from '@mui/icons-material';
import {Alert, Box, Collapse, IconButton, LinearProgress, Table, TableBody, TableCell, TableHead, TableRow} from '@mui/material';
import React, {useState} from 'react';
import {runQuery} from './PaymentApiClient';
import {Payment} from './PaymentsTable';

const LOCALE = 'en-GB';

export interface PaymentDetailsResponse {
  readonly paymentDetails: {
    readonly timestamp: string,
    readonly description: string,
    readonly category: {
      readonly name: string
    }
  }
}

interface PaymentDetails {
  readonly timestamp: string,
  readonly description: string,
  readonly category: string
}

const PAYMENT_DETAILS_QUERY = gql`
  query GetPaymentDetails($paymentId: String!) {
    paymentDetails (paymentId: $paymentId) {
      timestamp
      description
      category {
        name
      }
    }
  }
`;

const mapPaymentDetails = (result: ApolloQueryResult<PaymentDetailsResponse>): PaymentDetails => {
  const {timestamp, description, category} = result.data.paymentDetails;
  const date = new Date(timestamp);
  return {
    description,
    timestamp: date.toLocaleDateString(LOCALE) + ' ' + date.toLocaleTimeString(LOCALE),
    category: category.name,
  };
};

const PaymentRow = ({payment}: {payment: Payment}) => {

  const [details, setDetails] = useState<PaymentDetails>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>();

  const [expanded, setExpanded] = useState(false);

  const onExpand = (expanded: boolean) => {
    setExpanded(expanded);
    if (expanded && !details) {
      setLoading(true);
      runQuery(PAYMENT_DETAILS_QUERY, {paymentId: payment.id})
        .then(result => {
          setDetails(mapPaymentDetails(result));
          setError(undefined);
        })
        .catch(error => {
          console.error(error);
          setError(error.message);
        })
        .finally(() => setLoading(false));
    }
  };

  return (
    <React.Fragment>
      <TableRow>
        <TableCell data-test-ignore>
          <IconButton size="small"
                      onClick={() => onExpand(!expanded)}>
            {expanded ? <KeyboardArrowUp/> : <KeyboardArrowDown/>}
          </IconButton>
        </TableCell>
        <TableCell>{payment.amount}</TableCell>
        <TableCell>{payment.currency}</TableCell>
      </TableRow>
      <TableRow>
        <TableCell colSpan={3}>
          <Collapse in={expanded} unmountOnExit>
            <Box className="payment-row__expandable-box">
              {loading && <LinearProgress variant="query"/>}
              {error && <Alert severity='error'>Fetching payment details failed. Please, contact with the administrator.</Alert>}
              {details &&
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Date</TableCell>
                      <TableCell>Category</TableCell>
                      <TableCell>Description</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    <TableRow>
                      <TableCell>{details.timestamp}</TableCell>
                      <TableCell>{details.category}</TableCell>
                      <TableCell>{details.description}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              }
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  );
};

export default PaymentRow;