import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication/';
import { ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import {useStompClient, useSubscription} from 'react-stomp-hooks';
import useChatPreviewHandler from '../hooks/useChatPreviewHandler';
import useMessageHandler from '../hooks/useMessageHandler';
import useOnlineIndicator from '../hooks/useOnlineIndicator';
import useSubscriptionManager from '../hooks/useSubscriptionManager';
import useTypingIndicator from '../hooks/useTypingIndicator';
import { WebSocketContext } from './websocket-context';
import {ConnectionUpdateMessage} from "@features/chat/types";

interface WebSocketConnectionManagerProps {
  children: ReactNode;
  user: User;
}

export default function WebSocketConnectionManager({ children, user }: WebSocketConnectionManagerProps) {
  const stompClient = useStompClient();
  const currentUser = user;

  const [isConnected, setIsConnected] = useState(false);

  const [messageQueue, setMessageQueue] = useState<ChatMessageResponseDTO[]>([]);

  const [connectionUpdates, setConnectionUpdates] = useState<ConnectionUpdateMessage[]>([]);

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

  const handleNewMessage = useCallback((message: ChatMessageResponseDTO) => {
    console.log('🔄 ConnectionManager: Adding message to queue:', message);
    setMessageQueue((prevQueue) => [...prevQueue, message]);
  }, []);

  const clearMessageQueue = useCallback(() => {
    console.log('🧹 ConnectionManager: Clearing message queue');
    setMessageQueue([]); // Reset the queue to an empty array
  }, []);

  // Create stable handlers first
  const { handleMessage, sendMessage, sendMarkRead } = useMessageHandler({
    stompClient,
    currentUser,
    onMessageReceived: handleNewMessage,
  });

  const { typingUsers, handleTypingIndicator, sendTypingIndicator } = useTypingIndicator({
    stompClient,
    currentUser,
  });

  const { chatPreviews, handleChatPreviews } = useChatPreviewHandler();

  const { onlineUsers, handleOnlineIndicator } = useOnlineIndicator();

  useSubscription(`/user/${currentUser.id}/queue/connectionUpdates`, (message) => {
    console.log("Received WebSocket message:", message.body);
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

  const sendConnectionRequest = useCallback((targetUserId: number) => {
    if (stompClient?.connected && currentUser.id) {
      stompClient.publish({
        destination: '/app/connection.sendRequest',
        body: JSON.stringify(targetUserId),
      });
    } else {
      console.error('STOMP client not connected or user ID missing, cannot send connection request.');
    }
  }, [stompClient, currentUser.id]);

  const acceptConnectionRequest = useCallback((connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.acceptRequest',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected, cannot accept connection request.');
    }
  }, [stompClient]);

  const rejectConnectionRequest = useCallback((connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.rejectRequest',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected, cannot reject connection request.');
    }
  }, [stompClient]);

  const disconnectConnection = useCallback((connectionId: number) => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: '/app/connection.disconnect',
        body: JSON.stringify(connectionId),
      });
    } else {
      console.error('STOMP client not connected, cannot disconnect connection.');
    }
  }, [stompClient]);

  // Context value with stable identity
  const contextValue = useMemo(
    () => ({
      connected: isConnected,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      reconnect,
      typingUsers,
      onlineUsers,
      chatPreviews: chatPreviews || [],
      connectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
      messageQueue,
      clearMessageQueue,
    }),
    [
      isConnected,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      reconnect,
      typingUsers,
      onlineUsers,
      chatPreviews,
      connectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
      messageQueue,
      clearMessageQueue,
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}
