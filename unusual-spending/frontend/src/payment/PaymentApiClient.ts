import {ApolloClient, DocumentNode, InMemoryCache} from '@apollo/client';
import {OperationVariables} from '@apollo/client/core/types';

// TODO use env from k8s (loaded dynamically)
const paymentClient = new ApolloClient({
  uri: 'http://localhost/api/payment/graphql',
  cache: new InMemoryCache(),
});

export const runQuery = (query: DocumentNode,
                         variables: OperationVariables,
                         forceFetch = false) =>
  paymentClient.query({
    query,
    variables,
    fetchPolicy: forceFetch ? 'network-only' : 'cache-first',
  })