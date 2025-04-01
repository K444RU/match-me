import { ChatPreviewResponseDTO } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { useWebSocket, WebSocketProvider } from '@/features/chat';
import { chatService } from '@/features/chat/';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { ChatContext } from './chat-context';

interface ChatProviderProps {
  children: React.ReactNode;
  wsUrl: string;
}

export const ChatProvider = ({ children, wsUrl }: ChatProviderProps) => {
  const { user } = useAuth();
  const [chats, setChats] = useState<ChatPreviewResponseDTO[]>([]);
  const [openChat, setOpenChat] = useState<ChatPreviewResponseDTO | null>(null);

  const refreshChats = useCallback(async () => {
    if (!user) return;
    try {
      const data = await chatService.getChatPreviews();
      setChats(data);
    } catch (error) {
      console.error('Failed to refresh chats: ', error);
    }
  }, [user]);

  return (
    <WebSocketProvider wsUrl={wsUrl}>
      <ChatProviderInner
        refreshChats={refreshChats}
        chats={chats}
        openChat={openChat}
        setChats={setChats}
        setOpenChat={setOpenChat}
      >
        {children}
      </ChatProviderInner>
    </WebSocketProvider>
  );
};

interface ChatProviderInnerProps {
  refreshChats: () => void;
  chats: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  setChats: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO[]>>;
  setOpenChat: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO | null>>;
  children: React.ReactNode;
}

const ChatProviderInner = ({
  refreshChats,
  chats,
  openChat,
  setChats,
  setOpenChat,
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

    setChats((prevChats) => {
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
  }, [chatPreviews, setChats]);

  // Update open chat if needed - in a separate effect to avoid conflicts
  useEffect(() => {
    if (!openChat || !chatPreviews?.length) return;

    const updatedChat = chatPreviews.find((p) => p.connectionId === openChat.connectionId);
    if (updatedChat) {
      setOpenChat(updatedChat);
    }
  }, [chatPreviews, openChat, setOpenChat]);

  // Create a stable context value
  const contextValue = useMemo(
    () => ({
      chatPreviews: chats,
      openChat,
      refreshChats,
      setOpenChat,
      sendMessage,
      sendTypingIndicator,
      sendMarkRead,
    }),
    [chats, openChat, refreshChats, setOpenChat, sendMessage, sendTypingIndicator, sendMarkRead]
  );

  return <ChatContext.Provider value={contextValue}>{children}</ChatContext.Provider>;
};
