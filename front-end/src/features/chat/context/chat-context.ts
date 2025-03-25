import { ChatPreviewResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { createContext, useContext } from 'react';

export interface ChatContextType {
  chatPreviews: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  refreshChats: () => void;
  setOpenChat: (chat: ChatPreviewResponseDTO | null) => void;
  sendMessage: (message: MessagesSendRequestDTO) => Promise<void>;
  sendTypingIndicator: (connectionId: string) => void;
}

// Default values for the context to avoid null checks
const defaultContext: ChatContextType = {
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
