import { ChatMessageResponseDTO, GetChatMessagesParams, MessagesSendRequestDTO } from '@/api/types';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { useAuth } from '@/features/authentication';
import { chatService, CommunicationContext, useWebSocket } from '@/features/chat';
import { useCallback, useContext, useEffect, useRef, useState } from 'react';
import { toast } from 'sonner';
import NoChat from './NoChat';
import OpenChatInput from './OpenChatInput';
import OpenChatMessages from './OpenChatMessages';

export default function OpenChat() {
  const { user } = useAuth();
  const [chatMessages, setChatMessages] = useState<ChatMessageResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // Get WebSocket context for sending messages via WebSocket
  const { sendMessage: sendWebSocketMessage, typingUsers } = useWebSocket();

  const communicationContext = useContext(CommunicationContext);
  const openChat = communicationContext?.openChat || null;

  const connectionId = openChat?.connectionId;
  const updateAllChats = communicationContext.updateAllChats;
  const allChats = communicationContext.allChats;
  const { hasMoreMessages, updateHasMoreMessages } = communicationContext;

  const isTyping = openChat ? typingUsers[openChat.connectedUserId] : false;

  const fetchInitialMessages = useCallback(async (connectionId: number) => {
    if (!user) return;

    setLoading(true);
    setChatMessages([]);
    setIsLoadingMore(false);

    if (allChats[connectionId] && allChats[connectionId].length > 0) {
      setChatMessages(allChats[connectionId]);
      setLoading(false);
    } else {
      try {
        const getChatMessagesParams: GetChatMessagesParams = {
          pageable: {
            page: 0,
            size: 10,
          },
        };

        const pagedMessagesResponse = await chatService.getChatMessages(connectionId, getChatMessagesParams);
        const messagesResponse = pagedMessagesResponse.content;

        if (!messagesResponse || messagesResponse.length === 0) {
          setChatMessages([]);
          if (updateHasMoreMessages && connectionId) {
            updateHasMoreMessages(connectionId, false);
          }
          return;
        }

        const sortedMessages = messagesResponse.reverse();
        setChatMessages(sortedMessages);

        if (messagesResponse.length < 10 || pagedMessagesResponse.last) {
          if (updateHasMoreMessages && connectionId) {
            updateHasMoreMessages(connectionId, false);
          }
        }

        if (updateAllChats) {
          updateAllChats(connectionId, sortedMessages, true);
        }
      } catch (error) {
        console.error('Error fetching initial messages: ', error);
        if (updateHasMoreMessages && connectionId) {
          updateHasMoreMessages(connectionId, false);
        }
      } finally {
        setLoading(false);
      }
    }
  }, [user, allChats, updateAllChats, updateHasMoreMessages]);

  const loadOlderMessages = useCallback(async () => {
    if (!connectionId || !user || isLoadingMore || hasMoreMessages[connectionId] || chatMessages.length === 0) return;

    setIsLoadingMore(true);
    try {

      const getChatMessagesParams: GetChatMessagesParams = {
        pageable: {
          page: Math.floor(chatMessages.length / 10),
          size: 10,
        },
      };
      
      //parameters: connectionId, beforeMessageId or timestamp, limit, hasOlderMessages
      const pagedOlderMessagesResponse = await chatService.getChatMessages(connectionId, getChatMessagesParams);
      const olderMessagesResponse = pagedOlderMessagesResponse.content;

      if (!olderMessagesResponse || olderMessagesResponse.length === 0) {
        if (updateHasMoreMessages && connectionId) {
          updateHasMoreMessages(connectionId, false);
        }
        return;
      }

      const sortedMessages = olderMessagesResponse.reverse();
      const completeMessages = [...sortedMessages, ...chatMessages];


      setChatMessages(completeMessages);
      

      if (olderMessagesResponse.length < 10 || pagedOlderMessagesResponse.last) {
        if (updateHasMoreMessages && connectionId) {
          updateHasMoreMessages(connectionId, false);
        }
      }

      if (updateAllChats) {
        updateAllChats(connectionId, completeMessages, true);
      }
    } catch (error) {
      console.error('Error fetching older messages:', error);
      if (updateHasMoreMessages && connectionId) {
        updateHasMoreMessages(connectionId, false);
      }
    } finally {
      setIsLoadingMore(false);
    }
  }, [connectionId, user, isLoadingMore, hasMoreMessages, chatMessages, updateAllChats, updateHasMoreMessages]);

  useEffect(() => {
    if (connectionId) {
      fetchInitialMessages(connectionId);
    } else {
      setChatMessages([]);
      setLoading(false);
      setIsLoadingMore(false);
    }
    
  }, [connectionId]);

  useEffect(() => {
    if (connectionId && allChats[connectionId]) {
      setChatMessages(allChats[connectionId]);
    }
  }, [allChats, connectionId])

  // Early return if no context, user or open chat
  if (!communicationContext || !user) return null;

  const onSendMessage = async (message: string) => {
    if (!message || !user || !openChat) return;

    const messageDTO: MessagesSendRequestDTO = {
      connectionId: openChat.connectionId,
      content: message,
    };

    // Optimistic update first
    const optimisticMessage: ChatMessageResponseDTO = {
      connectionId: messageDTO.connectionId,
      content: messageDTO.content,
      createdAt: Math.floor(Date.now() / 1000).toString(),
      messageId: -(chatMessages.length + 1),
      senderAlias: user.alias || '',
      senderId: user.id || 0,
      event: {
        type: 'SENT',
        timestamp: new Date().toISOString(),
      },
    };

    if (communicationContext?.updateAllChats) {
      communicationContext.updateAllChats(openChat.connectionId, [optimisticMessage]);
    }

    setChatMessages(prevMessages => [...prevMessages, optimisticMessage]);

    try {
      await sendWebSocketMessage(messageDTO);
    } catch (error) {
      console.error('Error sending message:', error);
      // Fall back to REST API if GraphQL mutation fails
      try {
        console.warn('⚠️ GraphQL mutation failed, falling back to REST API');
        await chatService.sendMessage(messageDTO);
      } catch (restError) {
        console.error('REST fallback also failed:', restError);
        toast.error('Failed to send message.');
      }
    }
  };

  return (
    <div className="relative flex h-full w-full">
      <SidebarTrigger className="absolute left-1 top-1 z-10 lg:hidden" />
      {openChat ? (
        <div className="flex w-full flex-col bg-background-400 px-4 sm:px-6 md:px-8">
          <OpenChatMessages 
            loading={loading} 
            chatMessages={chatMessages} 
            user={user} 
            loadOlderMessages={loadOlderMessages}
            hasMoreMessages={openChat.connectionId ? hasMoreMessages[openChat.connectionId] ?? true : false}
            isLoadingMore={isLoadingMore}
            scrollContainerRef={scrollContainerRef}
            connectionId={openChat.connectionId}
            recipientAvatar={openChat.connectedUserProfilePicture}
          />
          <OpenChatInput
            onSendMessage={onSendMessage}
            isTyping={isTyping}
            recipientAlias={openChat.connectedUserAlias}
          />
        </div>
      ) : (
        <NoChat className="pl-16 lg:pl-4" />
      )}
    </div>
  );
}
