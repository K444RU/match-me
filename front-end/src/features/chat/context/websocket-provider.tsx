import { useAuth } from '@/features/authentication/';
import { ReactNode, useMemo } from 'react';
import { StompSessionProvider } from 'react-stomp-hooks';
import WebSocketConnectionManager from './connection-manager';

interface WebSocketProviderProps {
  children: ReactNode;
  wsUrl: string;
}

export const WebSocketProvider = ({ children, wsUrl }: WebSocketProviderProps) => {
  const { user: currentUser } = useAuth();

  const connectHeaders = useMemo(
    () => ({
      Authorization: `Bearer ${currentUser?.token || ''}`,
    }),
    [currentUser?.token]
  );

  // Simple early return if no user
  if (!currentUser?.id || !currentUser?.token) {
    return null;
  }

  return (
    <StompSessionProvider
      url={wsUrl}
      connectHeaders={connectHeaders}
      reconnectDelay={5000}
      heartbeatIncoming={4000}
      heartbeatOutgoing={4000}
      onStompError={(frame) => {
        console.error('WebSocket error:', frame);
      }}
    >
      <WebSocketConnectionManager user={currentUser}>{children}</WebSocketConnectionManager>
    </StompSessionProvider>
  );
};
