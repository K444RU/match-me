import {ChatPreviewResponseDTO} from '@/api/types';
import {useAuth} from '@/features/authentication';
import {useWebSocket, WebSocketProvider} from '@/features/chat';
import {chatService} from '@/features/chat/';
import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {CommunicationContext} from './communication-context.ts';
import {ConnectionUpdateMessage} from "@features/chat/types";

interface GlobalCommunicationProviderProps {
  children: React.ReactNode;
  wsUrl: string;
}

export const GlobalCommunicationProvider = ({ children, wsUrl }: GlobalCommunicationProviderProps) => {
  const { user } = useAuth();
  const [chats, setChats] = useState<ChatPreviewResponseDTO[]>([]);
  const [openChat, setOpenChat] = useState<ChatPreviewResponseDTO | null>(null);
  const [connectionUpdates, setConnectionUpdates] = useState<ConnectionUpdateMessage[]>([]);

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
      <GlobalCommunicationProviderInner
        refreshChats={refreshChats}
        chats={chats}
        openChat={openChat}
        setChats={setChats}
        setOpenChat={setOpenChat}
        connectionUpdates={connectionUpdates}
        setConnectionUpdates={setConnectionUpdates}
      >
        {children}
      </GlobalCommunicationProviderInner>
    </WebSocketProvider>
  );
};

interface GlobalCommunicationProviderInnerProps {
  refreshChats: () => void;
  chats: ChatPreviewResponseDTO[];
  openChat: ChatPreviewResponseDTO | null;
  setChats: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO[]>>;
  setOpenChat: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO | null>>;
  connectionUpdates: ConnectionUpdateMessage[];
  setConnectionUpdates: React.Dispatch<React.SetStateAction<ConnectionUpdateMessage[]>>;
  children: React.ReactNode;
}

const GlobalCommunicationProviderInner = ({
  refreshChats,
  chats,
  openChat,
  setChats,
  setOpenChat,
  connectionUpdates,
  setConnectionUpdates,
  children,
}: GlobalCommunicationProviderInnerProps) => {
  // Get websocket values (which include incoming chat previews and send functions)
    const {
        chatPreviews,
        sendMessage,
        sendTypingIndicator,
        connectionUpdates: wsConnectionUpdates,
        sendConnectionRequest,
        acceptConnectionRequest,
        rejectConnectionRequest,
        disconnectConnection } = useWebSocket();
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

    useEffect(() => {
        if (wsConnectionUpdates?.length) {
            setConnectionUpdates((prev) => [...prev, ...wsConnectionUpdates]);
        }
    }, [wsConnectionUpdates, setConnectionUpdates]);

  // Create a stable context value
  const contextValue = useMemo(
    () => ({
      chatPreviews: chats,
      openChat,
      refreshChats,
      setOpenChat,
      sendMessage,
      sendTypingIndicator,
      connectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    }),
      [chats, openChat, refreshChats, setOpenChat, sendMessage, sendTypingIndicator, connectionUpdates, sendConnectionRequest, acceptConnectionRequest, rejectConnectionRequest, disconnectConnection]  );

  return <CommunicationContext.Provider value={contextValue}>{children}</CommunicationContext.Provider>;
};
