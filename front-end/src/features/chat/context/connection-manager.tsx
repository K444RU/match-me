import { User } from '@/features/authentication/';
import { ReactNode, useEffect, useMemo, useState } from 'react';
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

  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const currentConnected = !!stompClient?.connected;

    if (currentConnected === isConnected) return;

    let timeoutId: NodeJS.Timeout;

    if (!currentConnected) {
      setIsConnected(false);
    } else {
      // add a bit of throtelling to avoid flickering
      timeoutId = setTimeout(() => {
        setIsConnected(true);
      }, 500);
    }

    return () => {
      if (timeoutId) clearTimeout(timeoutId);
    };
  }, [stompClient?.connected, isConnected]);

  // Create stable handlers first
  const { messages, handleMessage, sendMessage, sendMarkRead } = useMessageHandler({
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
      connected: isConnected,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      reconnect,
      messages,
      typingUsers,
      onlineUsers,
      chatPreviews: chatPreviews || [],
    }),
    [
      isConnected,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      reconnect,
      messages,
      typingUsers,
      onlineUsers,
      chatPreviews,
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}
