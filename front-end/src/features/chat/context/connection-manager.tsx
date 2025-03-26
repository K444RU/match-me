import { User } from '@/features/authentication/';
import { ReactNode, useMemo } from 'react';
import { useStompClient } from 'react-stomp-hooks';
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

  // Setup subscriptions with the handlers
  const { reconnect } = useSubscriptionManager({
    userId: currentUser?.id,
    stompClient,
    handleMessage,
    handleTypingIndicator,
    handleChatPreviews,
    handleOnlineIndicator,
  });

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
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}
