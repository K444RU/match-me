import { useAuth } from '@/features/authentication';
import { GlobalCommunicationProvider } from '@/features/chat/';
import WebSocketConnectionManager from '@/features/chat/context/connection-manager';
import client from '@/lib/apolloClient';
import { ApolloProvider } from '@apollo/client';
import { Outlet } from 'react-router-dom';

export default function AuthenticatedLayout() {
  const { user } = useAuth();

  if (!user) return;

  return (
    <ApolloProvider client={client}>
      <WebSocketConnectionManager user={user}>
        <GlobalCommunicationProvider>
          <div className="flex size-full overflow-hidden">
            <Outlet />
          </div>
        </GlobalCommunicationProvider>
      </WebSocketConnectionManager>
    </ApolloProvider>
  );
}
