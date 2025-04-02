import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { createContext, useContext } from 'react';

export interface ChatContextType {
  chatPreviews: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  allChats: Record<number, ChatMessageResponseDTO[]>;
  refreshChats: () => void;
  setOpenChat: (chat: ChatPreviewResponseDTO | null) => void;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: number) => void;
  sendMarkRead: (connectionId: number) => void;
  updateAllChats: (connectionId: number, messages: ChatMessageResponseDTO[]) => void;
}

// Default values for the context to avoid null checks
const defaultContext: ChatContextType = {
  chatPreviews: [],
  openChat: null,
  allChats: {},
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
  sendMarkRead: () => {
    console.warn('ChatContext not initialized');
  },
  updateAllChats: () => {
    console.warn('ChatContext not initialized');
  },
};

export const ChatContext = createContext<ChatContextType>(defaultContext);

export const useChat = (): ChatContextType => {
  const context = useContext(ChatContext);
  if (!context) {
    console.error('useChat must be used within a ChatProvider');
    return defaultContext;
  }
  return context;
};
