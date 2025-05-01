import { STORAGE_KEYS } from '@/lib/constants/storageKeys';
import { ApolloClient, createHttpLink, InMemoryCache, split } from '@apollo/client';
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

interface ChatPreview {
  connectionId?: string;
  [key: string]: any;
}

const cache = new InMemoryCache({
  typePolicies: {
    Subscription: {
      fields: {
        chatPreviews: {
          // Properly merge chat previews by connectionId
          merge(existing = [], incoming = []) {
            // Create a map of existing previews by connectionId
            const existingMap = new Map<string, ChatPreview>();
            if (Array.isArray(existing)) {
              existing.forEach((preview) => {
                const typedPreview = preview as ChatPreview;
                if (typedPreview && typedPreview.connectionId) {
                  existingMap.set(typedPreview.connectionId, typedPreview);
                }
              });
            } else if (existing && typeof existing === 'object') {
              // Handle case where existing is an object with numeric keys
              Object.values(existing).forEach((preview) => {
                const typedPreview = preview as ChatPreview;
                if (typedPreview && typedPreview.connectionId) {
                  existingMap.set(typedPreview.connectionId, typedPreview);
                }
              });
            }

            // Update map with incoming previews
            if (Array.isArray(incoming)) {
              incoming.forEach((preview) => {
                const typedPreview = preview as ChatPreview;
                if (typedPreview && typedPreview.connectionId) {
                  existingMap.set(typedPreview.connectionId, typedPreview);
                }
              });
            } else if (incoming && typeof incoming === 'object') {
              // Handle case where incoming is an object with numeric keys
              Object.values(incoming).forEach((preview) => {
                const typedPreview = preview as ChatPreview;
                if (typedPreview && typedPreview.connectionId) {
                  existingMap.set(typedPreview.connectionId, typedPreview);
                }
              });
            }

            // Convert back to array
            return Array.from(existingMap.values());
          },
        },
      },
    },
  },
});

const client = new ApolloClient({
  link: link,
  cache: cache,
});

export default client;
