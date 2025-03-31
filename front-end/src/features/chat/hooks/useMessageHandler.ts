import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { useCallback, useState } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';

interface UseMessageHandlerProps {
  stompClient: Client | undefined;
  currentUser: User;
}

export default function useMessageHandler({ stompClient, currentUser }: UseMessageHandlerProps) {
  const [messages, setMessages] = useState<ChatMessageResponseDTO[]>([]);

  const handleMessage = useCallback((message: IMessage) => {
    // Parse message and update state logic
    try {
      const data = JSON.parse(message.body) as ChatMessageResponseDTO;

      // Add validation to ensure the message is valid
      if (!data || typeof data !== 'object') {
        console.warn('Received invalid message format', message.body);
        return;
      }

      setMessages((prev) => {
        // Prevent duplicates
        const isDuplicate = prev.some(
          (m) => m.messageId === data.messageId || (m.createdAt === data.createdAt && m.content === data.content)
        );

        if (isDuplicate) {
          return prev;
        }

        return [...prev, data];
      });
    } catch (error) {
      console.error('Error parsing message:', error, message.body);
    }
  }, []);

  const sendMessage = useCallback(
    async (message: MessagesSendRequestDTO): Promise<void> => {
      if (!stompClient?.connected) {
        console.error('Cannot send message: WebSocket not connected');
        throw new Error('WebSocket not connected');
      }

      if (!currentUser?.id) {
        console.error('Cannot send message: User not authenticated');
        throw new Error('User not authenticated');
      }

      return new Promise((resolve, reject) => {
        try {
          // Ensure connectionId is a number
          const connectionId =
            typeof message.connectionId === 'string' ? parseInt(message.connectionId, 10) : message.connectionId;

          if (isNaN(connectionId)) {
            throw new Error(`Invalid connectionId: ${message.connectionId}`);
          }

          console.log('Sending message:', message);

          stompClient.publish({
            destination: '/app/chat.sendMessage',
            body: JSON.stringify(message),
            headers: {
              'content-type': 'application/json',
            },
          });

          resolve();
        } catch (error) {
          console.error('Error sending message:', error);
          reject(error);
        }
      });
    },
    [stompClient, currentUser]
  );

  return { messages, handleMessage, sendMessage };
}
