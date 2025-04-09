import {ChatMessageResponseDTO, ChatPreviewResponseDTO} from '@/api/types';
import {useAuth} from '@/features/authentication';
import {useWebSocket} from '@/features/chat';
import {chatService} from '@/features/chat/';
import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {CommunicationContext} from './communication-context.ts';
import {ConnectionUpdateMessage} from "@features/chat/types";

interface GlobalCommunicationProviderProps {
    children: React.ReactNode;
}

export const GlobalCommunicationProvider = ({children}: GlobalCommunicationProviderProps) => {
    const {user} = useAuth();
    const [chatDisplays, setChatDisplays] = useState<ChatPreviewResponseDTO[]>([]);
    const [openChat, setOpenChat] = useState<ChatPreviewResponseDTO | null>(null);
    const [connectionUpdates, setConnectionUpdates] = useState<ConnectionUpdateMessage[]>([]);
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
        <GlobalCommunicationProviderInner
            refreshChats={refreshChats}
            chatDisplays={chatDisplays}
            allChats={allChats}
            openChat={openChat}
            setChatDisplays={setChatDisplays}
            setOpenChat={setOpenChat}
            setAllChats={setAllChats}
            connectionUpdates={connectionUpdates}
            setConnectionUpdates={setConnectionUpdates}
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
    connectionUpdates: ConnectionUpdateMessage[];
    setConnectionUpdates: React.Dispatch<React.SetStateAction<ConnectionUpdateMessage[]>>;
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
                                              connectionUpdates,
                                              setConnectionUpdates,
                                              setAllChats,
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

    useEffect(() => {
        if (wsConnectionUpdates.length > 0) {
            const latestUpdate = wsConnectionUpdates[wsConnectionUpdates.length - 1];
            if (latestUpdate.action === 'REQUEST_ACCEPTED') {
                //TODO: Add Reject state here. these two currently working fine
                refreshChats();
            } else if (latestUpdate.action === 'DISCONNECTED') {
                setChatDisplays((prevChats) =>
                    prevChats.filter((chat) => chat.connectionId !== latestUpdate.connection.connectionId)
                );
            }
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
        if (wsConnectionUpdates?.length) {
            setConnectionUpdates((prev) => [...prev, ...wsConnectionUpdates]);
        }
    }, [wsConnectionUpdates, setConnectionUpdates]);

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
            refreshChats,
            setOpenChat,
            sendMessage,
            sendTypingIndicator,
            sendMarkRead,
            updateAllChats,
            connectionUpdates,
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
            connectionUpdates,
            sendConnectionRequest,
            acceptConnectionRequest,
            rejectConnectionRequest,
            disconnectConnection
        ]
    );

    return <CommunicationContext.Provider value={contextValue}>{children}</CommunicationContext.Provider>;
};
