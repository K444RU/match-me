import { ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { createContext, useContext } from 'react';
import {ConnectionUpdateMessage} from "@features/chat/types";

export interface CommunicationContextType {
  chatPreviews: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  refreshChats: () => void;
  setOpenChat: (chat: ChatPreviewResponseDTO | null) => void;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: string) => void;
  connectionUpdates: ConnectionUpdateMessage[];
  sendConnectionRequest: (targetUserId: number) => void;
  acceptConnectionRequest: (connectionId: number) => void;
  rejectConnectionRequest: (connectionId: number) => void;
  disconnectConnection: (connectionId: number) => void;
}

// Default values for the context to avoid null checks
const defaultContext: CommunicationContextType = {
  chatPreviews: [],
  openChat: null,
  refreshChats: () => {
    console.warn('ChatContext not initialized');
  },
  setOpenChat: () => {
    console.warn('ChatContext not initialized');
  },
  sendMessage: async () => {
    console.warn('ChatContext not initialized');
  },
  sendTypingIndicator: () => {
    console.warn('ChatContext not initialized');
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
    console.error('useChat must be used within a ChatProvider');
    return defaultContext;
  }
  return context;
};
