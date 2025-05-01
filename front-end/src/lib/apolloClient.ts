import { STORAGE_KEYS } from '@/lib/constants/storageKeys';
import {
  ApolloClient,
  createHttpLink,
  InMemoryCache,
  split,
} from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { getMainDefinition } from '@apollo/client/utilities';
import { createClient } from 'graphql-ws';

const httpLink = createHttpLink({
  uri: import.meta.env.VITE_GRAPHQL_URL,
});

const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);

  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

const wsLink = import.meta.env.VITE_GRAPHQL_WS_URL
  ? new GraphQLWsLink(
      createClient({
        url: import.meta.env.VITE_GRAPHQL_WS_URL,
        connectionParams: () => {
          const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
          return {
            Authorization: token ? `Bearer ${token}` : '',
          };
        },
      })
    )
  : null;

const link = wsLink
  ? split(
      ({ query }) => {
        const definition = getMainDefinition(query);
        const isSubscription = definition.kind === 'OperationDefinition' && definition.operation === 'subscription';
        return isSubscription;
      },
      wsLink,
      authLink.concat(httpLink)
    )
  : authLink.concat(httpLink);

const client = new ApolloClient({
  link: link,
  cache: new InMemoryCache(),
});

export default client;
