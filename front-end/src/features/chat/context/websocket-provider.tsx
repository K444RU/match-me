import { useAuth } from '@/features/authentication/';
import { ReactNode } from 'react';
import WebSocketConnectionManager from './connection-manager';

interface WebSocketProviderProps {
  children: ReactNode;
}

export const WebSocketProvider = ({ children }: WebSocketProviderProps) => {
  const { user: currentUser } = useAuth();

  if (!currentUser?.id || !currentUser?.token) {
    console.log('[WebSocketProvider] Skipping render: No user ID.');
    return null;
  }

  return <WebSocketConnectionManager user={currentUser}>{children}</WebSocketConnectionManager>;
};
