import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessageEventTypeEnum } from '@/api/types';
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
  const {
    chatPreviews,
    sendMessage,
    sendTypingIndicator,
    sendMarkRead,
    messageQueue,
    clearMessageQueue,
    statusUpdateQueue,
    clearStatusUpdateQueue,
  } = useWebSocket();

  // Load initial data once when connected
  useEffect(() => {
    refreshChats();
  }, [refreshChats]);

  // Merge websocket previews into our state when they change
  useEffect(() => {
    if (!chatPreviews?.length) {
      return;
    }

    setChatDisplays((prevChats) => {
      // Fast lookup map
      const chatMap = new Map(prevChats.map((chat) => [chat.connectionId, chat]));

      // Add new chats from websocket
      for (const preview of chatPreviews) {
        if (preview.connectionId <= 0) continue;

        chatMap.set(preview.connectionId, preview);
      }

      const mergedChats = Array.from(chatMap.values());

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

        if (connectionId === openChat?.connectionId && newMessages.length > 0) {
          sendMarkRead(connectionId);
        }

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

  // Add useEffect to process status updates
  useEffect(() => {
    if (!statusUpdateQueue || statusUpdateQueue.length === 0) return;

    let updated = false;
    setAllChats((prevAllChats) => {
      const newAllChats = { ...prevAllChats };

      for (const update of statusUpdateQueue) {
        const connectionMessages = newAllChats[update.connectionId];
        if (!connectionMessages) {
          continue; // Chat not loaded yet
        }

        const messageIndex = connectionMessages.findIndex((msg) => msg.messageId === update.messageId);
        if (messageIndex === -1) {
          continue; // Message not found (maybe optimistic?)
        }

        // Apply the update
        const existingMessage = connectionMessages[messageIndex];
        if (existingMessage.event.timestamp !== update.timestamp) {
          // Prevent re-processing same update if somehow duplicated
          const updatedMessage = {
            ...existingMessage,
            event: {
              type: update.type,
              timestamp: update.timestamp,
            },
          };
          newAllChats[update.connectionId] = [
            ...connectionMessages.slice(0, messageIndex),
            updatedMessage,
            ...connectionMessages.slice(messageIndex + 1),
          ];
          updated = true;
        }
      }
      return updated ? newAllChats : prevAllChats; // Only update state if changes were made
    });

    // Clear the queue after processing
    if (updated) {
      clearStatusUpdateQueue();
    }
  }, [statusUpdateQueue, setAllChats, clearStatusUpdateQueue]);

  const updateAllChats = useCallback(
    (connectionId: number, messages: ChatMessageResponseDTO[], replace: boolean = false) => {
      setAllChats((prev) => {
        // If there are already messages cached
        if (replace || !prev[connectionId]) {
          return {
            ...prev,
            [connectionId]: messages,
          };
        }

        return {
          ...prev,
          [connectionId]: [...prev[connectionId], ...messages],
        };
      });
    },
    []
  );

  const updateMessageStatus = useCallback(
    (connectionId: number, messageId: number, eventType: MessageEventTypeEnum, timestamp: string) => {
      setAllChats((prevAllChats) => {
        if (!prevAllChats[connectionId]) return prevAllChats;

        const updatedMessages = prevAllChats[connectionId].map((msg) => {
          if (msg.messageId === messageId) {
            return {
              ...msg,
              event: {
                type: eventType,
                timestamp,
              },
            };
          }
          return msg;
        });

        return {
          ...prevAllChats,
          [connectionId]: updatedMessages,
        };
      });
    },
    []
  );

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
      updateMessageStatus,
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
      updateMessageStatus,
    ]
  );

  return <ChatContext.Provider value={contextValue}>{children}</ChatContext.Provider>;
};
