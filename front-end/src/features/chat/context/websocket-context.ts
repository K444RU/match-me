import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { createContext, useContext } from 'react';

export interface WebSocketContextType {
  connected: boolean;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: number) => void;
  sendMarkRead: (connectionId: number) => void;
  reconnect: () => void;
  typingUsers: Record<string, boolean>;
  onlineUsers: Record<string, boolean>;
  chatPreviews: ChatPreviewResponseDTO[];
  messageQueue: ChatMessageResponseDTO[];
  clearMessageQueue: () => void;
}

const defaultContext: WebSocketContextType = {
  connected: false,
  sendMessage: async () => {
    console.error('WebSocket context not initialized');
  },
  sendTypingIndicator: () => {
    console.error('WebSocket context not initialized');
  },
  sendMarkRead: () => {
    console.error('WebSocket context not initialized');
  },
  reconnect: () => {
    console.error('WebSocket context not initialized');
  },
  clearMessageQueue: () => {
    console.error('WebSocket context not initialized');
  },
  typingUsers: {},
  onlineUsers: {},
  chatPreviews: [],
  messageQueue: [],
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
