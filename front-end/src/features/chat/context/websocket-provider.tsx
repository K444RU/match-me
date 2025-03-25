import { useAuth } from '@/features/authentication/';
import { ReactNode } from 'react';
import { StompSessionProvider } from 'react-stomp-hooks';
import WebSocketConnectionManager from './connection-manager';

interface WebSocketProviderProps {
  children: ReactNode;
  wsUrl: string;
}

export const WebSocketProvider = ({ children, wsUrl }: WebSocketProviderProps) => {
  const { user: currentUser } = useAuth();

  // Simple early return if no user
  if (!currentUser?.id) {
    return null;
  }

  return (
    <StompSessionProvider
      url={wsUrl}
      connectHeaders={{ Authorization: `Bearer ${currentUser.token}` }}
      debug={(msg: string) => console.log('STOMP DEBUG:', msg)}
      reconnectDelay={5000}
      heartbeatIncoming={4000}
      heartbeatOutgoing={4000}
      onStompError={(frame) => {
        console.log('STOMP error:', frame);
      }}
    >
      <WebSocketConnectionManager user={currentUser}>{children}</WebSocketConnectionManager>
    </StompSessionProvider>
  );
};
