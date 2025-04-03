import { ChatMessageResponseDTO, ChatPreviewResponseDTO } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { useWebSocket } from '@/features/chat';
import { chatService } from '@/features/chat/';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { ChatContext } from './chat-context';

interface ChatProviderProps {
  children: React.ReactNode;
}

export const ChatProvider = ({ children }: ChatProviderProps) => {
  const { user } = useAuth();
  const [chatDisplays, setChatDisplays] = useState<ChatPreviewResponseDTO[]>([]);
  const [openChat, setOpenChat] = useState<ChatPreviewResponseDTO | null>(null);
  const [allChats, setAllChats] = useState<Record<number, ChatMessageResponseDTO[]>>({});

  const refreshChats = useCallback(async () => {
    if (!user) return;
    try {
      const data = await chatService.getChatPreviews();
      setChatDisplays(data);
    } catch (error) {
      console.error('Failed to refresh chats: ', error);
    }
  }, [user]);

  return (
    <ChatProviderInner
      refreshChats={refreshChats}
      chatDisplays={chatDisplays}
      allChats={allChats}
      openChat={openChat}
      setChatDisplays={setChatDisplays}
      setOpenChat={setOpenChat}
      setAllChats={setAllChats}
    >
      {children}
    </ChatProviderInner>
  );
};

interface ChatProviderInnerProps {
  refreshChats: () => void;
  chatDisplays: ChatPreviewResponseDTO[];
  allChats: Record<number, ChatMessageResponseDTO[]>;
  openChat: ChatPreviewResponseDTO | null;
  setChatDisplays: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO[]>>;
  setOpenChat: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO | null>>;
  setAllChats: React.Dispatch<React.SetStateAction<Record<number, ChatMessageResponseDTO[]>>>;
  children: React.ReactNode;
}

const ChatProviderInner = ({
  refreshChats,
  chatDisplays,
  allChats,
  openChat,
  setChatDisplays,
  setOpenChat,
  setAllChats,
  children,
}: ChatProviderInnerProps) => {
  // Get websocket values (which include incoming chat previews and send functions)
  const { chatPreviews, sendMessage, sendTypingIndicator, sendMarkRead } = useWebSocket();

  // Load initial data once when connected
  useEffect(() => {
    refreshChats();
  }, [refreshChats]);

  // Merge websocket previews into our state when they change
  useEffect(() => {
    if (!chatPreviews?.length) {
      console.log('No websocket chat previews to process');
      return;
    }

    console.log('Merging websocket previews into state:', chatPreviews.length);

    setChatDisplays((prevChats) => {
      // Fast lookup map
      const chatMap = new Map(prevChats.map((chat) => [chat.connectionId, chat]));

      // Add new chats from websocket
      for (const preview of chatPreviews) {
        if (preview.connectionId <= 0) continue;

        chatMap.set(preview.connectionId, preview);
      }

      const mergedChats = Array.from(chatMap.values());
      console.log('Merged chats result:', mergedChats.length);

      // Sort by timestamp (newest first)
      return mergedChats.sort((a, b) => {
        const dateA = new Date(a.lastMessageTimestamp || 0);
        const dateB = new Date(b.lastMessageTimestamp || 0);
        return dateB.getTime() - dateA.getTime();
      });
    });
  }, [chatPreviews, setChatDisplays]);

  // Update open chat if needed - in a separate effect to avoid conflicts
  useEffect(() => {
    if (!openChat || !chatPreviews?.length) return;

    const updatedChat = chatPreviews.find((p) => p.connectionId === openChat.connectionId);
    if (updatedChat) {
      setOpenChat(updatedChat);
    }
  }, [chatPreviews, openChat, setOpenChat]);

  const updateAllChats = useCallback((connectionId: number, messages: ChatMessageResponseDTO[]) => {
    setAllChats((prev) => {
      // If there are already messages cached
      if (prev[connectionId]) {
        return {
          ...prev,
          [connectionId]: [...prev[connectionId], ...messages],
        };
      }

      return {
        ...prev,
        [connectionId]: messages,
      };
    });
  }, []);

  // Create a stable context value
  const contextValue = useMemo(
    () => ({
      chatPreviews: chatDisplays,
      openChat,
      allChats,
      refreshChats,
      setOpenChat,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      updateAllChats,
    }),
    [
      chatDisplays,
      openChat,
      allChats,
      refreshChats,
      setOpenChat,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
      updateAllChats,
    ]
  );

  return <ChatContext.Provider value={contextValue}>{children}</ChatContext.Provider>;
};
