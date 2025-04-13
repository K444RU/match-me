import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication/';
import { ReactNode, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useStompClient } from 'react-stomp-hooks';
import useChatPreviewHandler from '../hooks/useChatPreviewHandler';
import useMessageHandler from '../hooks/useMessageHandler';
import useOnlineIndicator from '../hooks/useOnlineIndicator';
import useSubscriptionManager from '../hooks/useSubscriptionManager';
import useTypingIndicator from '../hooks/useTypingIndicator';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';
import useConnectionRequestManager from '../hooks/useConnectionRequestManager';
import { WebSocketContext } from './websocket-context';

interface WebSocketConnectionManagerProps {
  children: ReactNode;
  user: User;
}

export default function WebSocketConnectionManager({ children, user }: WebSocketConnectionManagerProps) {
  const stompClient = useStompClient();
  const stompClientRef = useRef(stompClient);

  useEffect(() => {
    if (stompClient !== stompClientRef.current) {
      stompClientRef.current = stompClient;
    }
  }, [stompClient]);

  const currentUser = user;
  const [isConnected, setIsConnected] = useState(false);
  const [messageQueue, setMessageQueue] = useState<ChatMessageResponseDTO[]>([]);
  const [statusUpdateQueue, setStatusUpdateQueue] = useState<MessageStatusUpdateDTO[]>([]);

  // Connection management
  const {
    connectionUpdates,
    sendConnectionRequest,
    acceptConnectionRequest,
    rejectConnectionRequest,
    disconnectConnection,
  } = useConnectionRequestManager({ userId: currentUser.id, stompClient });

  useEffect(() => {
    const currentConnected = !!stompClientRef.current?.connected;

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
    setMessageQueue((prevQueue) => [...prevQueue, message]);
  }, []);

  const handleNewMessageStatusUpdate = useCallback((statusUpdate: MessageStatusUpdateDTO) => {
    setStatusUpdateQueue((prevQueue) => [...prevQueue, statusUpdate]);
  }, []);

  const clearMessageQueue = useCallback(() => {
    setMessageQueue([]); // Reset the queue to an empty array
  }, []);

  const clearStatusUpdateQueue = useCallback(() => {
    setStatusUpdateQueue([]);
  }, []);

  // Create stable handlers first
  const { handleMessage, handleMessageStatusUpdate, sendMessage, sendMarkRead } = useMessageHandler({
    stompClientRef,
    currentUser,
    onMessageReceived: handleNewMessage,
    onMessageStatusUpdateReceived: handleNewMessageStatusUpdate,
  });

  const { typingUsers, handleTypingIndicator, sendTypingIndicator } = useTypingIndicator({
    stompClientRef,
    currentUser,
  });

  const { chatPreviews, handleChatPreviews } = useChatPreviewHandler();
  const { onlineUsers, handleOnlineIndicator } = useOnlineIndicator();

  // Subscription management
  const { reconnect } = useSubscriptionManager({
    userId: currentUser?.id,
    stompClientRef,
    stompClientConnected: !!stompClientRef.current?.connected,
    handleMessage,
    handleMessageStatusUpdate,
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
      statusUpdateQueue,
      clearStatusUpdateQueue,
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
      statusUpdateQueue,
      clearStatusUpdateQueue,
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}