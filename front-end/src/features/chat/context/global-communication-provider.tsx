import { ChatMessageResponseDTO, ChatPreviewResponseDTO, MessageEventTypeEnum } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { useWebSocket } from '@/features/chat';
import { chatService } from '@/features/chat/';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { CommunicationContext } from '@features/chat';
import { userService } from '@features/user';
import { toast } from 'sonner';


interface GlobalCommunicationProviderProps {
  children: React.ReactNode;
}

export const GlobalCommunicationProvider = ({ children }: GlobalCommunicationProviderProps) => {
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

  // Pass all state and setters down to the inner provider
  return (
    <GlobalCommunicationProviderInner
      refreshChats={refreshChats}
      chatDisplays={chatDisplays}
      allChats={allChats}
      openChat={openChat}
      setChatDisplays={setChatDisplays}
      setOpenChat={setOpenChat}
      setAllChats={setAllChats}
    >
      {children}
    </GlobalCommunicationProviderInner>
  );
};

interface GlobalCommunicationProviderInnerProps {
  refreshChats: () => void;
  chatDisplays: ChatPreviewResponseDTO[];
  allChats: Record<number, ChatMessageResponseDTO[]>;
  openChat: ChatPreviewResponseDTO | null;
  setChatDisplays: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO[]>>;
  setOpenChat: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO | null>>;
  setAllChats: React.Dispatch<React.SetStateAction<Record<number, ChatMessageResponseDTO[]>>>;
  children: React.ReactNode;
}

const GlobalCommunicationProviderInner = ({
  refreshChats,
  chatDisplays,
  allChats,
  openChat,
  setChatDisplays,
  setOpenChat,
  setAllChats,
  children,
}: GlobalCommunicationProviderInnerProps) => {
  // Destructure all needed values from useWebSocket
  const {
    chatPreviews,
    sendMessage,
    sendTypingIndicator,
    sendMarkRead,
    messageQueue,
    clearMessageQueue,
    statusUpdateQueue,
    clearStatusUpdateQueue,
    connectionUpdates: wsConnectionUpdates,
    sendConnectionRequest,
    acceptConnectionRequest,
    rejectConnectionRequest,
    disconnectConnection,
  } = useWebSocket();

  // Ref from master
  const processedUpdatesCountRef = useRef(0);

  // Load initial data (common effect)
  useEffect(() => {
    refreshChats();
  }, [refreshChats]);

  // Merge websocket previews (common effect, using cleaner version without extra logs)
  useEffect(() => {
    if (!chatPreviews?.length) {
      return;
    }

    setChatDisplays((prevChats) => {
      const chatMap = new Map(prevChats.map((chat) => [chat.connectionId, chat]));
      for (const preview of chatPreviews) {
        if (preview.connectionId <= 0) continue;
        chatMap.set(preview.connectionId, preview);
      }
      const mergedChats = Array.from(chatMap.values());
      return mergedChats.sort((a, b) => {
        const dateA = new Date(a.lastMessageTimestamp || 0);
        const dateB = new Date(b.lastMessageTimestamp || 0);
        return dateB.getTime() - dateA.getTime();
      });
    });
  }, [chatPreviews, setChatDisplays]);

  // Handle Connection updates & trigger Toasts notifications (from master)
  useEffect(() => {
    const currentLength = wsConnectionUpdates.length;
    // Ensure we don't process already handled updates if the array reference changes but content is the same initially
    const lastProcessedIndex = Math.min(processedUpdatesCountRef.current, currentLength);
    const newUpdates = wsConnectionUpdates.slice(lastProcessedIndex);


    if (newUpdates.length > 0) {
      console.log(`[GlobalCommProvider] Processing ${newUpdates.length} new connection updates:`, newUpdates);

      newUpdates.forEach(update => {
         // Basic validation for the update structure
         if (!update || !update.action || !update.connection || typeof update.connection.userId !== 'number' || typeof update.connection.connectionId !== 'number') {
          console.warn("[GlobalCommProvider] Received invalid update structure:", update);
          return; // Skip processing this invalid update
        }

        const otherUserId = update.connection.userId;
        const connectionId = update.connection.connectionId;

        console.log("[GlobalCommProvider] Processing update:", update.action, "for connection:", connectionId, "involving user:", otherUserId);


        switch (update.action) {
          case 'REQUEST_ACCEPTED':
            console.log("[GlobalCommProvider] Accepted detected, refreshing chats & showing toast.");
            userService.getUser(otherUserId)
              .then(acceptedUser => {
                const alias = acceptedUser?.alias || `User ${otherUserId}`;
                toast.success(`🤝 Connection with ${alias} accepted!`);
              })
              .catch(err => {
                console.error(`Failed to fetch user ${otherUserId} for accept toast`, err);
                toast.success(`🤝 Connection accepted!`);
              });
            refreshChats(); // Refresh chat list to show the new connection
            break;

          case 'DISCONNECTED':
            console.log("[GlobalCommProvider] Disconnected detected, filtering chatDisplay & showing toast for connection:", connectionId);
            // Fetch user info for the toast *before* potentially removing the chat
             userService.getUser(otherUserId)
               .then(disconnectedUser => {
                 const alias = disconnectedUser?.alias || `User ${otherUserId}`;
                 toast.error(`🔌 Disconnected from ${alias}.`);
               })
               .catch(err => {
                 console.error(`Failed to fetch user ${otherUserId} for disconnect toast`, err);
                 toast.error(`🔌 Disconnected.`);
               });
            // Remove the disconnected chat from the display list
            setChatDisplays((prevChats) => prevChats.filter((chat) => chat.connectionId !== connectionId));
            // Optionally remove messages from allChats as well
            setAllChats(prev => {
              const newState = {...prev};
              delete newState[connectionId];
              return newState;
            })
            // If the disconnected chat was open, close it
            if (openChat?.connectionId === connectionId) {
              setOpenChat(null);
            }
            break;

          case 'NEW_REQUEST':
             console.log("[GlobalCommProvider] New request detected, triggering toast.");
            // We might not need to fetch user details here, a generic notification might suffice
            toast.info(`📬 New connection request received! Check your connection requests.`);
            // Optionally trigger a refresh or update a badge elsewhere
            break;

          case 'REQUEST_REJECTED':
            console.log("[GlobalCommProvider] Rejected detected, showing toast.");
            userService.getUser(otherUserId)
              .then(otherUser => {
                const alias = otherUser?.alias || `User ${otherUserId}`;
                toast.warning(`🙅 Your connection request involving ${alias} was rejected.`);
              })
              .catch(err => {
                console.error(`Failed to fetch user ${otherUserId} for reject toast`, err);
                toast.warning(`🙅 A connection request was rejected.`);
              });
            // Maybe refresh connection status/requests if applicable
            break;

          case 'REQUEST_SENT':
             console.log("[GlobalCommProvider] Request Sent detected, showing confirmation toast.");
             // Fetch user details for confirmation toast
             userService.getUser(otherUserId)
              .then(targetUser => {
                  const alias = targetUser?.alias || `User ${otherUserId}`;
                  toast.success(`✅ Connection request sent to ${alias}.`);
              })
              .catch(err => {
                  console.error(`Failed to fetch user ${otherUserId} for sent toast`, err);
                  toast.success('✅ Connection request sent.');
              });
             break;


          default:
            // Ensure exhaustive check or handle unknown actions
            console.warn("[GlobalCommProvider] Received unhandled connection update action:", update.action, update);
            // const _exhaustiveCheck: never = update.action; // Use for type checking if action type is an enum
        }
      });
      // Update ref *after* processing the new updates
      processedUpdatesCountRef.current = currentLength;
    }
  }, [wsConnectionUpdates, refreshChats, setChatDisplays, setAllChats, openChat, setOpenChat]);

  // Update open chat if needed
  useEffect(() => {
    if (!openChat || !chatPreviews?.length) return;
    const updatedChat = chatPreviews.find((p) => p.connectionId === openChat.connectionId);
    if (updatedChat) {
      setOpenChat(updatedChat);
    }
  }, [chatPreviews, openChat, setOpenChat]);

  // Process message queue
  useEffect(() => {
    if (!messageQueue || messageQueue.length === 0) return;

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

        // If messages are for the currently open chat, mark them as read
        if (connectionId === openChat?.connectionId && newMessages.length > 0) {
           // Consider debouncing or checking if already marked read if needed
           sendMarkRead(connectionId);
        }


        // filter out optimistic messages that might still be in state
        const existingRealMessages = existingMessages.filter((msg) => msg.messageId > 0);

        // filter out potential duplicates based on messageId
        const existingRealMessageIds = new Set(existingRealMessages.map((msg) => msg.messageId));

        const uniqueNewMessages = newMessages.filter((newMsg) => !existingRealMessageIds.has(newMsg.messageId));

        // Append unique new messages
        newAllChats[connectionId] = [...existingRealMessages, ...uniqueNewMessages];
      }
      return newAllChats;
    });

    clearMessageQueue();
  }, [messageQueue, setAllChats, clearMessageQueue, openChat, sendMarkRead]);

  // Process status updates
  useEffect(() => {
    if (!statusUpdateQueue || statusUpdateQueue.length === 0) return;

    let updated = false;
    setAllChats((prevAllChats) => {
      const newAllChats = { ...prevAllChats };

      for (const update of statusUpdateQueue) {
        const connectionMessages = newAllChats[update.connectionId];
        if (!connectionMessages) {
          continue; // Chat history not loaded yet for this connection
        }

        const messageIndex = connectionMessages.findIndex((msg) => msg.messageId === update.messageId);
        if (messageIndex === -1) {
           // Could be an optimistic message not yet replaced, or message not loaded.
           console.warn(`[GlobalCommProvider] Status update received for unknown messageId ${update.messageId} in connection ${update.connectionId}`);
          continue;
        }

        // Apply the update if it's newer or different
        const existingMessage = connectionMessages[messageIndex];
        // Ensure we only update if the timestamp is different to avoid redundant updates
        if (existingMessage.event?.timestamp !== update.timestamp) {
          const updatedMessage = {
            ...existingMessage,
            event: {
              type: update.type,
              timestamp: update.timestamp,
            },
          };
          // Create a new array for the updated messages to ensure state immutability
          newAllChats[update.connectionId] = [
            ...connectionMessages.slice(0, messageIndex),
            updatedMessage,
            ...connectionMessages.slice(messageIndex + 1),
          ];
          updated = true;
        }
      }
      // Only return a new object reference if changes were actually made
      return updated ? newAllChats : prevAllChats;
    });

    // Clear the queue only if updates were processed
    if (updated) {
      clearStatusUpdateQueue();
    }
  }, [statusUpdateQueue, setAllChats, clearStatusUpdateQueue]);

  // Callback to update messages for a connection
  const updateAllChats = useCallback(
    (connectionId: number, messages: ChatMessageResponseDTO[], replace: boolean = false) => {
      setAllChats((prev) => {
        const currentMessages = prev[connectionId] || [];
        // Decide whether to replace or append based on the 'replace' flag or if no messages exist yet
        const newMessages = replace ? messages : [...currentMessages, ...messages];

        // Optimization: Filter duplicates just in case (e.g., if called manually after fetch)
        const uniqueMessages = Array.from(new Map(newMessages.map(msg => [msg.messageId, msg])).values()).sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()); // Sort by timestamp


        // Only update state if the messages actually changed
        if (prev[connectionId] === uniqueMessages) {
          return prev; // No change, return previous state
        }


        return {
          ...prev,
          [connectionId]: uniqueMessages,
        };
      });
    },
    [setAllChats]
  );


  // Callback to update a single message's status
  const updateMessageStatus = useCallback(
    (connectionId: number, messageId: number, eventType: MessageEventTypeEnum, timestamp: string) => {
      setAllChats((prevAllChats) => {
        const connectionMessages = prevAllChats[connectionId];
        // If the connection or its messages aren't in state, do nothing
        if (!connectionMessages) return prevAllChats;


        let messageFound = false;
        const updatedMessages = connectionMessages.map((msg) => {
          if (msg.messageId === messageId) {
            messageFound = true;
            // Only update if the event is different or new
            if (msg.event?.type !== eventType || msg.event?.timestamp !== timestamp) {
                return {
                ...msg,
                event: {
                    type: eventType,
                    timestamp,
                },
                };
            }
          }
          return msg;
        });

        // If the message was found and potentially updated, return the new state object
        if (messageFound) {
             // Avoid creating new state if no message was actually updated
            if (updatedMessages === connectionMessages) return prevAllChats;


            return {
            ...prevAllChats,
            [connectionId]: updatedMessages,
            };
        }


        // If messageId was not found, return the previous state
        return prevAllChats;
      });
    },
    [setAllChats]
  );

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
      connectionUpdates: wsConnectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
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
      wsConnectionUpdates,
      sendConnectionRequest,
      acceptConnectionRequest,
      rejectConnectionRequest,
      disconnectConnection,
    ]
  );

  return <CommunicationContext.Provider value={contextValue}>{children}</CommunicationContext.Provider>;
};