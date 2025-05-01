import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { ConnectionUpdateEvent } from '@features/chat/types';
import { createContext, useContext } from 'react';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';

export interface WebSocketContextType {
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: number) => void;
  sendMarkRead: (connectionId: number) => void;
  typingUsers: Record<number, boolean>;
  onlineUsers: Record<number, boolean>;
  chatPreviews: ChatPreviewResponseDTO[];
  messageQueue: ChatMessageResponseDTO[];
  clearMessageQueue: () => void;
  statusUpdateQueue: MessageStatusUpdateDTO[];
  clearStatusUpdateQueue: () => void;
  connectionUpdates: ConnectionUpdateEvent[];
  sendConnectionRequest: (targetUserId: string) => void;
  acceptConnectionRequest: (connectionId: string) => void;
  rejectConnectionRequest: (connectionId: string) => void;
  disconnectConnection: (connectionId: string) => void;
}

const defaultContext: WebSocketContextType = {
  sendMessage: async () => {
    console.error('WebSocket context not initialized');
  },
  sendTypingIndicator: () => {
    console.error('WebSocket context not initialized');
  },
  sendMarkRead: () => {
    console.error('WebSocket context not initialized');
  },
  typingUsers: {},
  onlineUsers: {},
  chatPreviews: [],
  messageQueue: [],
  clearMessageQueue: () => {},
  statusUpdateQueue: [],
  clearStatusUpdateQueue: () => {},
  connectionUpdates: [],
  sendConnectionRequest: () => console.error('WebSocket context not initialized'),
  acceptConnectionRequest: () => console.error('WebSocket context not initialized'),
  rejectConnectionRequest: () => console.error('WebSocket context not initialized'),
  disconnectConnection: () => console.error('WebSocket context not initialized'),
};

export const WebSocketContext = createContext<WebSocketContextType>(defaultContext);

export const useWebSocket = (): WebSocketContextType => {
  const context = useContext(WebSocketContext);
  if (!context) {
    console.error('useWebSocket must be used within WebSocketProvider');
    return defaultContext;
  }
  return context;
};
