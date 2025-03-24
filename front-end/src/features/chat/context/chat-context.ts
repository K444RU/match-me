import { ChatPreview } from '@/types/api';
import { createContext } from 'react';

interface ChatContext {
  chatPreviews: ChatPreview[] | null;
  openChat: number | null; // connectionId
  refreshChats: () => Promise<void>;
  setOpenChat: (chatId: number | null) => void;
}

export const ChatContext = createContext<ChatContext | null>(null);
