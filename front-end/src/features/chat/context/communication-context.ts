import {
  ChatMessageResponseDTO,
  ChatPreviewResponseDTO,
  MessageEventTypeEnum,
  MessagesSendRequestDTO,
} from '@/api/types';
import { createContext, useContext } from 'react';

export interface CommunicationContextType {
  chatPreviews: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  allChats: Record<number, ChatMessageResponseDTO[]>;
  refreshChats: () => void;
  setOpenChat: (chat: ChatPreviewResponseDTO | null) => void;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: number) => void;
  sendMarkRead: (connectionId: number) => void;
  updateAllChats: (connectionId: number, messages: ChatMessageResponseDTO[], replace?: boolean) => void;
  updateMessageStatus: (
    connectionId: number,
    messageId: number,
    eventType: MessageEventTypeEnum,
    timestamp: string
  ) => void;
}

// Default values for the context to avoid null checks
const defaultContext: CommunicationContextType = {
  chatPreviews: [],
  openChat: null,
  allChats: {},
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
