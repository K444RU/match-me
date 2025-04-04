import { ChatMessageResponseDTO, ChatPreviewResponseDTO } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { useWebSocket } from '@/features/chat';
import { chatService } from '@/features/chat/';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
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
  const { chatPreviews, sendMessage, sendTypingIndicator, sendMarkRead, messageQueue, clearMessageQueue } =
    useWebSocket();

  // Load initial data once when connected
  useEffect(() => {
    refreshChats();
  }, [refreshChats]);

  // Merge websocket previews into state
  useEffect(() => {
    if (!chatPreviews?.length) {
      // console.log('No websocket chat previews to process');
      return;
    }

    // console.log('Merging websocket previews into state:', chatPreviews.length);

    setChatDisplays((prevChats) => {
      const chatMap = new Map(prevChats.map((chat) => [chat.connectionId, chat]));
      for (const preview of chatPreviews) {
        if (preview.connectionId <= 0) continue;
        chatMap.set(preview.connectionId, preview);
      }
      const mergedChats = Array.from(chatMap.values());
      // console.log('Merged chats result:', mergedChats.length);
      return mergedChats.sort((a, b) => {
        const dateA = new Date(a.lastMessageTimestamp || 0);
        const dateB = new Date(b.lastMessageTimestamp || 0);
        return dateB.getTime() - dateA.getTime();
      });
    });
  }, [chatPreviews, setChatDisplays]);

  // Update open chat preview if needed
  useEffect(() => {
    if (!openChat || !chatPreviews?.length) return;
    const updatedChat = chatPreviews.find((p) => p.connectionId === openChat.connectionId);
    if (updatedChat) {
      setOpenChat(updatedChat);
    }
  }, [chatPreviews, openChat, setOpenChat]);

  useEffect(() => {
    if (!messageQueue || messageQueue.length === 0) return;

    // Group messages in messageQueue by connectionId
    const messagesByConnection: Record<number, ChatMessageResponseDTO[]> = {};
    for (const message of messageQueue) {
      if (!messagesByConnection[message.connectionId]) {
        messagesByConnection[message.connectionId] = [];
      }
      messagesByConnection[message.connectionId].push(message);
    }

    setAllChats((prevAllChats) => {
      const newAllChats = { ...prevAllChats };
      for (const connectionIdStr in messagesByConnection) {
        const connectionId = parseInt(connectionIdStr, 10);
        const newMessages = messagesByConnection[connectionId];
        const existingMessages = newAllChats[connectionId] || [];

        // filter out optimistic messages
        const existingRealMessages = existingMessages.filter((msg) => msg.messageId > 0);

        // filter out potential duplicates
        const existingRealMessageIds = new Set(existingRealMessages.map((msg) => msg.messageId));

        const uniqueNewMessages = newMessages.filter((newMsg) => !existingRealMessageIds.has(newMsg.messageId));

        newAllChats[connectionId] = [...existingRealMessages, ...uniqueNewMessages];
      }
      return newAllChats;
    });

    clearMessageQueue();
  }, [messageQueue, setAllChats, clearMessageQueue]);

  const updateAllChats = useCallback(
    (connectionId: number, messagesToAdd: ChatMessageResponseDTO[], replace: boolean = false) => {
      setAllChats((prev) => {
        const existingMessages = prev[connectionId] || [];
        let updatedMessages;

        if (replace) {
          updatedMessages = messagesToAdd;
        } else {
          // Filter out duplicates before adding
          const existingMessageIds = new Set(existingMessages.map((m) => m.messageId));
          const newMessages = messagesToAdd.filter((m) => !existingMessageIds.has(m.messageId));
          updatedMessages = [...existingMessages, ...newMessages];
        }

        // Sort messages by date after updating
        updatedMessages.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());

        return {
          ...prev,
          [connectionId]: updatedMessages,
        };
      });
    },
    [setAllChats] // setAllChats is stable
  );

  // Process incoming WebSocket messages and update allChats
  useEffect(() => {
    if (!webSocketMessages || webSocketMessages.length === 0) {
      return;
    }

    // console.log(`Processing ${webSocketMessages.length} websocket messages...`);

    const newMessagesToProcess: Record<number, ChatMessageResponseDTO[]> = {};

    webSocketMessages.forEach((msg) => {
      // Check if messageId exists and hasn't been processed from the websocket stream yet
      if (msg.messageId && !processedMessageIds.current.has(msg.messageId)) {
        if (!newMessagesToProcess[msg.connectionId]) {
          newMessagesToProcess[msg.connectionId] = [];
        }
        newMessagesToProcess[msg.connectionId].push(msg);
        processedMessageIds.current.add(msg.messageId); // Mark as processed
      }
    });

    // Batch update allChats for each connectionId that received new messages
    Object.entries(newMessagesToProcess).forEach(([connIdStr, messages]) => {
      const connId = parseInt(connIdStr, 10);
      if (!isNaN(connId) && messages.length > 0) {
        // console.log(`Updating allChats for connection ${connId} with ${messages.length} new messages`);
        updateAllChats(connId, messages, false); // Append new messages
      }
    });
    // Depend on the webSocketMessages array identity and updateAllChats callback
  }, [webSocketMessages, updateAllChats]);

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
