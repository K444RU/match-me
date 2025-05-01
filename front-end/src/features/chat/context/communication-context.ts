import {
  ChatMessageResponseDTO,
  ChatPreviewResponseDTO,
  MessageEventTypeEnum,
  MessagesSendRequestDTO,
} from '@/api/types';
import { createContext, useContext } from 'react';
import { ConnectionUpdateEvent } from '@features/chat/types';

export interface CommunicationContextType {
  chatPreviews: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  allChats: Record<number, ChatMessageResponseDTO[]>;
  hasMoreMessages: Record<number, boolean>;
  sendMarkRead: (connectionId: number) => void;
  updateAllChats: (connectionId: number, messages: ChatMessageResponseDTO[], replace?: boolean) => void;
  updateHasMoreMessages: (connectionId: number, hasMore: boolean) => void;
  refreshChats: () => void;
  setOpenChat: (chat: ChatPreviewResponseDTO | null) => void;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: number) => void;
  updateMessageStatus: (
    connectionId: number,
    messageId: number,
    eventType: MessageEventTypeEnum,
    timestamp: string
  ) => void;
  typingUsers: Record<number, boolean>;
  onlineUsers: Record<number, boolean>;
  connectionUpdates: ConnectionUpdateEvent[];
  sendConnectionRequest: (targetUserId: string) => void;
  acceptConnectionRequest: (connectionId: string) => void;
  rejectConnectionRequest: (connectionId: string) => void;
  disconnectConnection: (connectionId: string) => void;
}

// Default values for the context to avoid null checks
const defaultContext: CommunicationContextType = {
  chatPreviews: [],
  openChat: null,
  allChats: {},
  hasMoreMessages: {},
  typingUsers: {},
  onlineUsers: {},
  refreshChats: () => {
    console.warn('CommunicationContext not initialized');
  },
  setOpenChat: () => {
    console.warn('CommunicationContext not initialized');
  },
  sendMessage: async () => {
    console.warn('CommunicationContext not initialized');
  },
  sendTypingIndicator: () => {
    console.warn('CommunicationContext not initialized');
  },
  sendMarkRead: () => {
    console.warn('CommunicationContext not initialized');
  },
  updateAllChats: () => {
    console.warn('CommunicationContext not initialized');
  },
  updateMessageStatus: () => {
    console.warn('CommunicationContext not initialized');
  },
  updateHasMoreMessages: () => {
    console.warn('CommunicationContext not initialized');
  },
  connectionUpdates: [],
  sendConnectionRequest: () => console.error('Communication context not initialized'),
  acceptConnectionRequest: () => console.error('Communication context not initialized'),
  rejectConnectionRequest: () => console.error('Communication context not initialized'),
  disconnectConnection: () => console.error('Communication context not initialized'),
};

export const CommunicationContext = createContext<CommunicationContextType>(defaultContext);

export const useCommunication = (): CommunicationContextType => {
  const context = useContext(CommunicationContext);
  if (!context) {
    console.error('useCommunication must be used within a CommunicationProvider');
    return defaultContext;
  }
  return context;
};
