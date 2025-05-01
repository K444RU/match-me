import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication/';
import { ReactNode, useCallback, useMemo, useState } from 'react';
import useChatPreviewHandler from '../hooks/useChatPreviewHandler';
import useConnectionRequestManager from '../hooks/useConnectionRequestManager';
import useMessageHandler from '../hooks/useMessageHandler';
import useOnlineIndicator from '../hooks/useOnlineIndicator';
import useTypingIndicator from '../hooks/useTypingIndicator';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';
import { WebSocketContext } from './websocket-context';

interface WebSocketConnectionManagerProps {
  children: ReactNode;
  user: User;
}

export default function WebSocketConnectionManager({ children, user }: WebSocketConnectionManagerProps) {
  const currentUser = user;
  const [messageQueue, setMessageQueue] = useState<ChatMessageResponseDTO[]>([]);
  const [statusUpdateQueue, setStatusUpdateQueue] = useState<MessageStatusUpdateDTO[]>([]);

  // Connection management
  const {
    connectionUpdates,
    sendConnectionRequest,
    acceptConnectionRequest,
    rejectConnectionRequest,
    disconnectConnection,
  } = useConnectionRequestManager();

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

  // Message handling
  const { sendMessage, sendMarkRead } = useMessageHandler({
    currentUser,
    onMessageReceived: handleNewMessage,
    onMessageStatusUpdateReceived: handleNewMessageStatusUpdate,
  });

  // Typing indicator
  const { typingUsers, sendTypingIndicator } = useTypingIndicator({
    currentUser,
  });

  // Chat previews
  const { chatPreviews } = useChatPreviewHandler({
    currentUser,
  });

  // Online status
  const { onlineUsers } = useOnlineIndicator({
    currentUser,
  });

  // Context value with stable identity
  const contextValue = useMemo(
    () => ({
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      typingUsers,
      onlineUsers,
      chatPreviews: chatPreviews || [],
      connectionUpdates,
      messageQueue,
      statusUpdateQueue,
      clearMessageQueue,
      clearStatusUpdateQueue,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    }),
    [
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      typingUsers,
      onlineUsers,
      chatPreviews,
      connectionUpdates,
      messageQueue,
      statusUpdateQueue,
      clearMessageQueue,
      clearStatusUpdateQueue,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    ]
  );

  return <WebSocketContext.Provider value={contextValue}>{children}</WebSocketContext.Provider>;
}
