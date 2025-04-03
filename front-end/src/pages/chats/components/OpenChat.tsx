import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import Button from '@/components/ui/buttons/Button';
import InputField from '@/components/ui/forms/InputField';
import { useAuth } from '@/features/authentication';
import { ChatContext, chatService, useWebSocket } from '@/features/chat';
import { useContext, useEffect, useRef, useState } from 'react';
import Message from './Message';
import { NoChat } from './NoChat';

export default function OpenChat() {
  const { user } = useAuth();
  const [message, setMessage] = useState('');
  const [chatMessages, setChatMessages] = useState<ChatMessageResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [hasMoreOlderMessages, setHasMoreOlderMessages] = useState(true);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // Get WebSocket context for sending messages via WebSocket
  const { connected, sendMessage: sendWebSocketMessage, sendTypingIndicator } = useWebSocket();

  const chatContext = useContext(ChatContext);
  const openChat = chatContext?.openChat || null;

  const scrollToBottom = () => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollTop = scrollContainerRef.current.scrollHeight;
    }
  };

  const handleScroll = async () => {
    const container = scrollContainerRef.current;
    if (!container) return;

    const SCROLL_THRESHOLD = 50;
    if (container.scrollTop <= SCROLL_THRESHOLD && !isLoadingMore && hasMoreOlderMessages && chatMessages.length > 0) {
      setIsLoadingMore(true);
      //const oldestMessageId = chatMessages[0]?.messageId;

      const oldScrollHeight = container.scrollHeight;
      const oldScrollTop = container.scrollTop;

      try {
        if (!openChat) return; // fix the api call logic
        const olderMessagesResponse = await chatService.getChatMessages(openChat.connectionId);

        if (olderMessagesResponse && olderMessagesResponse.length > 0) {
          const sortedOlderMessages = olderMessagesResponse.reverse();

          setChatMessages((prevMessages) => [...sortedOlderMessages, ...prevMessages]);
          setHasMoreOlderMessages(olderMessagesResponse.length === 20);

          requestAnimationFrame(() => {
            if (scrollContainerRef.current) {
              const newScrollHeight = scrollContainerRef.current.scrollHeight;
              scrollContainerRef.current.scrollTop = oldScrollTop + (newScrollHeight - oldScrollHeight);
            }
          });
        } else {
          setHasMoreOlderMessages(false);
        }
      } catch (error) {
        console.error('Error fetching older messages: ', error);
      } finally {
        setIsLoadingMore(false);
      }
    }
  };

  useEffect(() => {
    setChatMessages([]);
    setHasMoreOlderMessages(true);
    setIsLoadingMore(false);

    const fetchMessages = async () => {
      if (!openChat || !user) return;

      setLoading(true);

      // check if chat is in context already
      if (chatContext?.allChats[openChat.connectionId]?.length > 0) {
        setChatMessages(chatContext.allChats[openChat.connectionId]);

        setHasMoreOlderMessages(true);
        setLoading(false);
        requestAnimationFrame(scrollToBottom);
      } else {
        try {
          // Have to adapt endpoint to support infinite scroll
          const messagesResponse = await chatService.getChatMessages(openChat.connectionId);

          if (!messagesResponse || messagesResponse.length === 0) {
            setChatMessages([]);
            setHasMoreOlderMessages(false);
            return;
          }

          const sortedMessages = messagesResponse.reverse();
          setChatMessages(sortedMessages);

          setHasMoreOlderMessages(messagesResponse.length === 20);

          requestAnimationFrame(scrollToBottom);

          if (chatContext?.updateAllChats) {
            chatContext.updateAllChats(openChat.connectionId, sortedMessages, true);
          }
        } catch (error) {
          console.error('Error fetching messages:', error);
          setHasMoreOlderMessages(false);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchMessages();
  }, [openChat, user, chatContext]);

  useEffect(() => {
    scrollToBottom();
  }, [chatMessages]);

  // Early return if no context, user or open chat
  if (!chatContext) return null;
  if (!user) return null;
  if (!openChat) return <NoChat />;

  const handleSendMessage = async (message: MessagesSendRequestDTO) => {
    if (!message || !user || !openChat) return;

    try {
      // Only use WebSocket if already connected
      if (connected) {
        console.log('🚀 Sending message via WebSocket');
        try {
          await sendWebSocketMessage(message);
        } catch (wsError) {
          console.error('Failed to send via WebSocket:', wsError);
        }
      } else {
        console.warn('⚠️ WebSocket not connected, message sent only via REST');
        await chatService.sendMessage(message.content, message.connectionId);
      }

      // Optimistically add the message to the UI
      const newMessage: ChatMessageResponseDTO = {
        connectionId: message.connectionId,
        content: message.content,
        createdAt: new Date().toISOString(),
        messageId: chatMessages.length + 1,
        senderAlias: user.alias || '',
        senderId: user.id || 0,
      };

      const updatedMessages = [...chatMessages, newMessage];
      setChatMessages(updatedMessages);

      if (chatContext?.updateAllChats) {
        chatContext.updateAllChats(openChat.connectionId, [newMessage]);
      }

      setMessage('');
    } catch (error) {
      console.error('Error sending message:', error);
      // Show error to user
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSendMessage({
        connectionId: openChat.connectionId,
        content: message,
      });
    } else if (connected && openChat && openChat.connectionId) {
      // Send typing indicator when user is typing
      sendTypingIndicator(openChat.connectionId);
    }
  };

  const handleMessageChange = (value: string) => {
    setMessage(value);

    // Only send typing indicator if already connected
    if (connected && openChat && openChat.connectionId) {
      sendTypingIndicator(openChat.connectionId);
    }
  };

  return (
    <div className="flex size-full flex-col bg-background-400 px-4 pb-4 sm:px-6 md:px-8">
      {/* onScroll handler */}
      <div ref={scrollContainerRef} className="mt-4 flex-1 overflow-y-scroll" onScroll={handleScroll}>
        {/* Loading indicator for older messages */}
        {isLoadingMore && (
          <div className="flex justify-center p-2 text-sm text-gray-500">Loading older messages...</div>
        )}
        {loading ? (
          <div className="flex justify-center p-4">Loading messages...</div>
        ) : chatMessages.length === 0 ? (
          <div className="flex justify-center p-4">No messages yet. Start the conversation!</div>
        ) : (
          chatMessages.map((msg) => (
            <Message key={`${msg.connectionId}-${msg.messageId}`} message={msg} isOwn={msg.senderId === user.id} />
          ))
        )}
      </div>
      <div className="mt-4 flex gap-2">
        <InputField
          placeholder="Aa"
          name="message"
          type="text"
          value={message}
          onChange={handleMessageChange}
          onKeyDown={handleKeyDown}
        />
        <Button
          onClick={() =>
            handleSendMessage({
              connectionId: openChat.connectionId,
              content: message,
            })
          }
        >
          Send
        </Button>
      </div>
    </div>
  );
}
