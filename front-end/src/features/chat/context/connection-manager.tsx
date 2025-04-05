import { User } from '@/features/authentication/';
import {ReactNode, useMemo, useState} from 'react';
import {useStompClient, useSubscription} from 'react-stomp-hooks';
import useChatPreviewHandler from '../hooks/useChatPreviewHandler';
import useMessageHandler from '../hooks/useMessageHandler';
import useOnlineIndicator from '../hooks/useOnlineIndicator';
import useSubscriptionManager from '../hooks/useSubscriptionManager';
import useTypingIndicator from '../hooks/useTypingIndicator';
import { WebSocketContext } from './websocket-context';

interface WebSocketConnectionManagerProps {
  children: ReactNode;
  user: User;
}

export default function WebSocketConnectionManager({ children, user }: WebSocketConnectionManagerProps) {
  const stompClient = useStompClient();
  const currentUser = user;

  // Create stable handlers first
  const { messages, handleMessage, sendMessage } = useMessageHandler({
    stompClient,
    currentUser,
  });

  const { typingUsers, handleTypingIndicator, sendTypingIndicator } = useTypingIndicator({
    stompClient,
    currentUser,
  });

  const { chatPreviews, handleChatPreviews } = useChatPreviewHandler();

  const { onlineUsers, handleOnlineIndicator } = useOnlineIndicator();

  const [connectionUpdates, setConnectionUpdates] = useState<any[]>([]);

  useSubscription(`/user/{currentUser.id}/queue/connectionUpdates`, (message) => {
    const update = JSON.parse(message.body);
    setConnectionUpdates((prev) => [...prev, update]);
  });

  // Setup subscriptions with the handlers
  const { reconnect } = useSubscriptionManager({
    userId: currentUser?.id,
    stompClient,
    handleMessage,
    handleTypingIndicator,
    handleChatPreviews,
    handleOnlineIndicator,
  });

  const sendConnectionRequest = (targetUserId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.sendRequest',
        body: JSON.stringify(targetUserId),
      });
    } else {
      console.error('STOMP client not connected');
    }
  };

  const acceptConnectionRequest = (connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.acceptRequest',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected');
    }
  };

  const rejectConnectionRequest = (connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.rejectRequest',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected');
    }
  };

  const disconnectConnection = (connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.disconnect',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected');
    }
  };

  // Context value with stable identity
  const contextValue = useMemo(
    () => ({
      connected: !!stompClient?.connected,
      sendMessage,
      sendTypingIndicator,
      reconnect,
      messages,
      typingUsers,
      onlineUsers,
      chatPreviews: chatPreviews || [],
      connectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    }),
    [
      stompClient?.connected,
      sendMessage,
      sendTypingIndicator,
      reconnect,
      messages,
      typingUsers,
      onlineUsers,
      chatPreviews,
      connectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}
