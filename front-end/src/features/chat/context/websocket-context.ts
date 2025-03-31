import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { createContext, useContext } from 'react';

export interface WebSocketContextType {
  connected: boolean;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: string) => void;
  reconnect: () => void;
  messages: ChatMessageResponseDTO[];
  typingUsers: Record<string, boolean>;
  onlineUsers: Record<string, boolean>;
  chatPreviews: ChatPreviewResponseDTO[];
}

const defaultContext: WebSocketContextType = {
  connected: false,
  sendMessage: async () => {
    console.error('WebSocket context not initialized');
  },
  sendTypingIndicator: () => {
    console.error('WebSocket context not initialized');
  },
  reconnect: () => {
    console.error('WebSocket context not initialized');
  },
  messages: [],
  typingUsers: {},
  onlineUsers: {},
  chatPreviews: [],
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
