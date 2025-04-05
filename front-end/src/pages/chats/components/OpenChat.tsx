import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { ChatContext, chatService, useWebSocket } from '@/features/chat';
import { useContext, useEffect, useState } from 'react';
import { NoChat } from './NoChat';
import OpenChatInput from './OpenChatInput';
import OpenChatMessages from './OpenChatMessages';

export default function OpenChat() {
  const { user } = useAuth();
  const [chatMessages, setChatMessages] = useState<ChatMessageResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);

  // Get WebSocket context for sending messages via WebSocket
  const { connected, sendMessage: sendWebSocketMessage } = useWebSocket();

  const chatContext = useContext(ChatContext);
  const openChat = chatContext?.openChat || null;

  const connectionId = openChat?.connectionId;
  const updateAllChats = chatContext.updateAllChats;
  const allChats = chatContext.allChats;

  useEffect(() => {
    setChatMessages([]);

    const fetchMessages = async () => {
      if (!connectionId || !user) return;

      setLoading(true);

      // check if chat is in context already
      if (allChats[connectionId]) {
        setChatMessages(allChats[connectionId]);
        setLoading(false);
      } else {
        try {
          const messagesResponse = await chatService.getChatMessages(connectionId);

          if (!messagesResponse) {
            setChatMessages([]);
            return;
          }

          const sortedMessages = messagesResponse.reverse();
          setChatMessages(sortedMessages);

          if (updateAllChats) {
            updateAllChats(connectionId, sortedMessages, true);
          }
        } catch (error) {
          console.error('Error fetching messages:', error);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchMessages();
  }, [connectionId, user, updateAllChats, allChats]);

  // Early return if no context, user or open chat
  if (!chatContext) return null;
  if (!user) return null;
  if (!openChat) return <NoChat />;

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
      createdAt: new Date().toISOString(),
      messageId: -(chatMessages.length + 1),
      senderAlias: user.alias || '',
      senderId: user.id || 0,
      event: {
        type: 'SENT',
        timestamp: new Date().toISOString(),
      },
    };

    if (chatContext?.updateAllChats) {
      chatContext.updateAllChats(openChat.connectionId, [optimisticMessage]);
    }

    try {
      // Only use WebSocket if already connected
      if (connected) {
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
    <div className="flex w-full flex-col bg-background-400 px-4 pb-4 sm:px-6 md:px-8">
      <OpenChatMessages loading={loading} chatMessages={chatMessages} user={user} />
      <OpenChatInput onSendMessage={onSendMessage} />
    </div>
  );
}
