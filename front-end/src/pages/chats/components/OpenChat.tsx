import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import Button from '@/components/ui/buttons/Button';
import InputField from '@/components/ui/forms/InputField';
import { useAuth } from '@/features/authentication';
import { ChatContext, chatService, useWebSocket } from '@/features/chat';
import { useContext, useEffect, useState } from 'react';
import Message from './Message';
import { NoChat } from './NoChat';

export default function OpenChat() {
  const { user } = useAuth();
  const [message, setMessage] = useState('');
  const [chatMessages, setChatMessages] = useState<ChatMessageResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);

  // Get WebSocket context for sending messages via WebSocket
  const { connected, sendMessage: sendWebSocketMessage, sendTypingIndicator, reconnect } = useWebSocket();

  const chatContext = useContext(ChatContext);
  const openChat = chatContext?.openChat || null;

  const [reconnectAttempted, setReconnectAttempted] = useState(false);

  // Only reconnect if the WebSocket is disconnected - use throtelling
  useEffect(() => {
    if (!user || !openChat) return;

    let reconnectTimer: NodeJS.Timeout;

    if (!connected && !reconnectAttempted) {
      reconnectTimer = setTimeout(() => {
        console.log('🔄 WebSocket disconnected, attempting to reconnect...');
        reconnect();
        setReconnectAttempted(false);
      }, 1000);
    } else if (connected && reconnectAttempted) {
      setReconnectAttempted(false);
    }

    return () => {
      if (reconnectTimer) clearTimeout(reconnectTimer);
    };
  }, [connected, reconnect]);

  useEffect(() => {
    const fetchMessages = async () => {
      if (!openChat || !user) return;

      setLoading(true);

      // check if chat is in context already
      if (chatContext?.allChats[openChat.connectionId]) {
        setChatMessages(chatContext.allChats[openChat.connectionId]);
        setLoading(false);
      } else {
        try {
          const messagesResponse = await chatService.getChatMessages(openChat.connectionId);

          if (!messagesResponse) {
            setChatMessages([]);
            return;
          }

          const sortedMessages = messagesResponse.reverse();
          setChatMessages(sortedMessages);

          if (chatContext?.updateAllChats) {
            chatContext.updateAllChats(openChat.connectionId, sortedMessages);
          }
        } catch (error) {
          console.error('Error fetching messages:', error);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchMessages();
  }, [openChat, user, chatContext]);

  // Early return if no context, user or open chat
  if (!chatContext) return null;
  if (!user) return null;
  if (!openChat) return <NoChat />;

  const handleSendMessage = async (message: MessagesSendRequestDTO) => {
    if (!message || !user || !openChat) return;

    try {
      // First send via REST API to ensure persistence
      await chatService.sendMessage(message.content, message.connectionId);

      // Only use WebSocket if already connected
      if (connected) {
        console.log('🚀 Sending message via WebSocket');
        try {
          await sendWebSocketMessage(message);
        } catch (wsError) {
          console.error('Failed to send via WebSocket:', wsError);
          // If WebSocket fails, at least the message is already persisted via REST
        }
      } else {
        console.warn('⚠️ WebSocket not connected, message sent only via REST');
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
    <div className="flex w-full flex-col bg-background-400 px-4 pb-4 sm:px-6 md:px-8">
      <div className="mt-4 size-full overflow-y-scroll">
        {loading ? (
          <div className="flex justify-center p-4">Loading messages...</div>
        ) : chatMessages.length === 0 ? (
          <div className="flex justify-center p-4">No messages yet. Start the conversation!</div>
        ) : (
          chatMessages.map((msg, index) => <Message key={index} message={msg} isOwn={msg.senderId === user.id} />)
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
