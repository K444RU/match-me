import {ChatMessageResponseDTO, ChatPreviewResponseDTO, MessagesSendRequestDTO} from '@/api/types';
import {createContext, useContext} from 'react';
import {ConnectionUpdateMessage} from "@features/chat/types";

export interface WebSocketContextType {
  connected: boolean;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: string) => void;
  reconnect: () => void;
  messages: ChatMessageResponseDTO[];
  typingUsers: Record<string, boolean>;
  onlineUsers: Record<string, boolean>;
  chatPreviews: ChatPreviewResponseDTO[];
  connectionUpdates: ConnectionUpdateMessage[];
  sendConnectionRequest: (targetUserId: number) => void;
  acceptConnectionRequest: (connectionId: number) => void;
  rejectConnectionRequest: (connectionId: number) => void;
  disconnectConnection: (connectionId: number) => void;
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
