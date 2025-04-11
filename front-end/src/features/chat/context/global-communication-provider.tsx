import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useAuth} from '@/features/authentication';
import {useWebSocket} from '@/features/chat';
import {ChatMessageResponseDTO, ChatPreviewResponseDTO} from '@/api/types';
import {CommunicationContext} from '@features/chat';
import {userService} from "@features/user";
import {chatService} from '@/features/chat/';
import { toast } from 'sonner';

interface GlobalCommunicationProviderProps {
    children: React.ReactNode;
}

export const GlobalCommunicationProvider = ({children}: GlobalCommunicationProviderProps) => {
    const {user} = useAuth();

    const [chatDisplays, setChatDisplays] = useState<ChatPreviewResponseDTO[]>([]);
    const [openChat, setOpenChat] = useState<ChatPreviewResponseDTO | null>(null);
    const [allChats, setAllChats] = useState<Record<number, ChatMessageResponseDTO[]>>({});
    const [hasMoreMessages, setHasMoreMessages] = useState<Record<number, boolean>>({});

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
        <GlobalCommunicationProviderInner
            refreshChats={refreshChats}
            chatDisplays={chatDisplays}
            allChats={allChats}
            openChat={openChat}
            hasMoreMessages={hasMoreMessages}
            setChatDisplays={setChatDisplays}
            setOpenChat={setOpenChat}
            setAllChats={setAllChats}
            setHasMoreMessages={setHasMoreMessages}
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
    hasMoreMessages: Record<number, boolean>;
    setChatDisplays: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO[]>>;
    setOpenChat: React.Dispatch<React.SetStateAction<ChatPreviewResponseDTO | null>>;
    setAllChats: React.Dispatch<React.SetStateAction<Record<number, ChatMessageResponseDTO[]>>>;
    setHasMoreMessages: React.Dispatch<React.SetStateAction<Record<number, boolean>>>;
    children: React.ReactNode;
}

const GlobalCommunicationProviderInner = ({
                                              refreshChats,
                                              chatDisplays,
                                              allChats,
                                              openChat,
                                              hasMoreMessages,
                                              setChatDisplays,
                                              setOpenChat,
                                              setAllChats,
                                              setHasMoreMessages,
                                              children,
                                          }: GlobalCommunicationProviderInnerProps) => {
    // Get websocket values (which include incoming chat previews and send functions)
    const {
        chatPreviews,
        sendMessage,
        sendTypingIndicator,
        sendMarkRead,
        messageQueue,
        clearMessageQueue,
        connectionUpdates: wsConnectionUpdates,
        sendConnectionRequest,
        acceptConnectionRequest,
        rejectConnectionRequest,
        disconnectConnection
    } = useWebSocket();

    const processedUpdatesCountRef = useRef(0);

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

    // Handle Connection updates & trigger Toasts notifications
    useEffect(() => {
        const currentLength = wsConnectionUpdates.length;
        const lastProcessedIndex = Math.min(processedUpdatesCountRef.current, currentLength);
        const newUpdates = wsConnectionUpdates.slice(lastProcessedIndex);

        if (newUpdates.length > 0) {
            console.log(`[GlobalCommProvider] Processing ${newUpdates.length} new connection updates:`, newUpdates);

            newUpdates.forEach(update => {
                if (!update || !update.action || !update.connection || typeof update.connection.userId !== 'number' || typeof update.connection.connectionId !== 'number') {
                    console.warn("[GlobalCommProvider] Received invalid update structure:", update);
                    return;
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
                        refreshChats();
                        break;

                    case 'DISCONNECTED':
                        console.log("[GlobalCommProvider] Disconnected detected, filtering chatDisplay & showing toast for connection:", connectionId);
                        userService.getUser(otherUserId)
                            .then(disconnectedUser => {
                                const alias = disconnectedUser?.alias || `User ${otherUserId}`;
                                toast.error(`🔌 Disconnected from ${alias}.`);
                            })
                            .catch(err => {
                                console.error(`Failed to fetch user ${otherUserId} for disconnect toast`, err);
                                toast.error(`🔌 Disconnected.`);
                            });
                        setChatDisplays((prevChats) => {
                            return prevChats.filter((chat) => chat.connectionId !== connectionId);
                        });
                        break;

                    case 'NEW_REQUEST':
                        console.log("[GlobalCommProvider] New request detected, triggering toast.");
                        toast.info(`📬 New connection request!`);
                        break;

                    case 'REQUEST_REJECTED':
                        console.log("[GlobalCommProvider] Rejected detected, showing toast.");
                        userService.getUser(otherUserId)
                            .then(otherUser => {
                                const alias = otherUser?.alias || `User ${otherUserId}`;
                                toast.warning(`🙅 Connection request involving ${alias} rejected.`);
                            })
                            .catch(err => {
                                console.error(`Failed to fetch user ${otherUserId} for reject toast`, err);
                                toast.warning(`🙅 Connection request rejected.`);
                            });
                        break;

                    case 'REQUEST_SENT':
                        console.log("[GlobalCommProvider] Request Sent detected, no action needed here.");
                        break;

                    default:
                        console.warn("[GlobalCommProvider] Received unhandled connection update action:", update.action);
                }
            });

            processedUpdatesCountRef.current = currentLength;
        }
    }, [wsConnectionUpdates, refreshChats, setChatDisplays]);


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
            const newAllChats = {...prevAllChats};
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

    const updateHasMoreMessages = useCallback(
      (connectionId: number, hasMore: boolean) => {
        setHasMoreMessages((prev) => ({
          ...prev,
          [connectionId]: hasMore,
        }));
      },
      [setHasMoreMessages]
    )

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

    // Create a stable context value
    const contextValue = useMemo(
        () => ({
            chatPreviews: chatDisplays,
            openChat,
            allChats,
            hasMoreMessages,
            refreshChats,
            setOpenChat,
            sendMessage,
            sendTypingIndicator,
            sendMarkRead,
            updateAllChats,
            updateHasMoreMessages,
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
            hasMoreMessages,
            refreshChats,
            setOpenChat,
            sendMessage,
            sendTypingIndicator,
            sendMarkRead,
            updateAllChats,
            updateHasMoreMessages,
            wsConnectionUpdates,
            sendConnectionRequest,
            acceptConnectionRequest,
            rejectConnectionRequest,
            disconnectConnection
        ]
    );

    return <CommunicationContext.Provider value={contextValue}>{children}</CommunicationContext.Provider>;
};
