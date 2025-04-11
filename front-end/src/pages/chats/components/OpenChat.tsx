import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { useAuth } from '@/features/authentication';
import { CommunicationContext, chatService, useWebSocket } from '@/features/chat';
import { useCallback, useContext, useEffect, useRef, useState } from 'react';
import NoChat from './NoChat';
import OpenChatInput from './OpenChatInput';
import OpenChatMessages from './OpenChatMessages';

export default function OpenChat() {
  const { user } = useAuth();
  const [chatMessages, setChatMessages] = useState<ChatMessageResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [hasMoreOlderMessages, setHasMoreOlderMessages] = useState(true);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // Get WebSocket context for sending messages via WebSocket
  const { connected, sendMessage: sendWebSocketMessage, typingUsers } = useWebSocket();

  const communicationContext = useContext(CommunicationContext);
  const openChat = communicationContext?.openChat || null;

  const connectionId = openChat?.connectionId;
  const updateAllChats = communicationContext.updateAllChats;
  const allChats = communicationContext.allChats;

  const isTyping = openChat ? typingUsers[openChat.connectedUserId] : false;

  const fetchInitialMessages = useCallback(async (connectionId: number) => {
    if (!user) return;

    setLoading(true);
    setChatMessages([]);
    setHasMoreOlderMessages(true);
    setIsLoadingMore(false);

    if (allChats[connectionId] && allChats[connectionId].length > 0) {
      setChatMessages(allChats[connectionId]);
      setLoading(false);
    } else {
      try {
        // need to add pagination support
        const messagesResponse = await chatService.getChatMessages(connectionId);

        if (!messagesResponse || messagesResponse.length === 0) {
          setChatMessages([]);
          setHasMoreOlderMessages(false);
          return;
        }

        const sortedMessages = messagesResponse.reverse();
        setChatMessages(sortedMessages);

        // need to check about this too
        if (messagesResponse.length < 20 ) {
          setHasMoreOlderMessages(false);
        }

        if (updateAllChats) {
          updateAllChats(connectionId, sortedMessages, true);
        }
      } catch (error) {
        console.error('Error fetching initial messages: ', error);
        setHasMoreOlderMessages(false);
      } finally {
        setLoading(false);
      }
    }
  }, [user, allChats, updateAllChats]);

  const loadOlderMessages = useCallback(async () => {
    if (!connectionId || !user || isLoadingMore || !hasMoreOlderMessages || chatMessages.length === 0) return;

    setIsLoadingMore(true);
    try {
      //const oldestMessage = chatMessages[0];
      
      //parameters: connectionId, beforeMessageId or timestamp, limit, hasOlderMessages
      const olderMessagesResponse = await chatService.getChatMessages(connectionId);

      if (!olderMessagesResponse || olderMessagesResponse.length === 0) {
        setHasMoreOlderMessages(false);
        return;
      }

      const sortedMessages = olderMessagesResponse.reverse();
      setChatMessages((prevMessages) => [...sortedMessages, ...prevMessages]);
      

      if (olderMessagesResponse.length < 20) {
        setHasMoreOlderMessages(false);
      }

      if (updateAllChats) {
        updateAllChats(connectionId, sortedMessages, true);
      }
    } catch (error) {
      console.error('Error fetching older messages:', error);
      setHasMoreOlderMessages(false);
    } finally {
      setIsLoadingMore(false);
    }
  }, [connectionId, user, isLoadingMore, hasMoreOlderMessages, chatMessages, updateAllChats]);

  useEffect(() => {
    if (connectionId) {
      fetchInitialMessages(connectionId);
    } else {
      setChatMessages([]);
      setHasMoreOlderMessages(false);
      setLoading(false);
      setIsLoadingMore(false);
    }
    
  }, [connectionId, fetchInitialMessages]);

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
    };

    if (communicationContext?.updateAllChats) {
      communicationContext.updateAllChats(openChat.connectionId, [optimisticMessage]);
    }

    try {
      // Only use WebSocket if already connected
      if (connected) {
        console.log('🚀 Sending message via WebSocket');
        try {
          await sendWebSocketMessage(messageDTO);
        } catch (wsError) {
          console.error('Failed to send via WebSocket:', wsError);
        }
      } else {
        console.warn('⚠️ WebSocket not connected, message sent only via REST');
        await chatService.sendMessage(messageDTO.content, messageDTO.connectionId);
      }
    } catch (error) {
      console.error('Error sending message:', error);
      // Show error to user
    }
  };

  return (
    <div className="relative flex h-screen w-full">
      <SidebarTrigger className="absolute left-1 top-1 z-10 lg:hidden" />
      {openChat ? (
        <div className="flex w-full flex-col bg-background-400 px-4 sm:px-6 md:px-8">
          <OpenChatMessages 
            loading={loading} 
            chatMessages={chatMessages} 
            user={user} 
            loadOlderMessages={loadOlderMessages}
            hasMoreOlderMessages={hasMoreOlderMessages}
            isLoadingMore={isLoadingMore}
            scrollContainerRef={scrollContainerRef}
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
