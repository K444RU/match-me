import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { useCallback } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';

interface UseMessageHandlerProps {
  stompClient: Client | undefined;
  currentUser: User;
  onMessageReceived: (message: ChatMessageResponseDTO) => void;
  onMessageStatusUpdateReceived: (message: MessageStatusUpdateDTO) => void;
}

export default function useMessageHandler({
  stompClient,
  currentUser,
  onMessageReceived,
  onMessageStatusUpdateReceived,
}: UseMessageHandlerProps) {
  const handleMessage = useCallback((message: IMessage) => {
    // Parse message and update state logic
    try {
      const data = JSON.parse(message.body) as ChatMessageResponseDTO;
      // TODO: mark as recieved
      // Add validation to ensure the message is valid
      if (!data || typeof data !== 'object') {
        console.warn('Received invalid message format', message.body);
        return;
      }

      onMessageReceived(data);
    } catch (error) {
      console.error('Error parsing message:', error, message.body);
    }
  }, []);

  const handleMessageStatusUpdate = useCallback(
    (message: IMessage) => {
      try {
        const data = JSON.parse(message.body) as MessageStatusUpdateDTO;
        if (!data || typeof data !== 'object') {
          console.warn('Received invalid message status format', message.body);
          return;
        }
        onMessageStatusUpdateReceived(data);
      } catch (error) {
        console.error('Error parsing message status:', error, message.body);
      }
    },
    [onMessageStatusUpdateReceived]
  );

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

  const sendMarkRead = useCallback(
    (connectionId: number) => {
      if (!stompClient?.connected || !currentUser?.id) {
        console.error('Cannot send markRead: STOMP client not connected or no user ID.');
        return;
      }
      try {
        stompClient.publish({
          destination: '/app/chat.markRead',
          body: JSON.stringify({ connectionId: connectionId.toString() }),
        });
        console.log(`Sent markRead for connection ${connectionId}`);
      } catch (error) {
        console.error(`Error sending markRead for connection ${connectionId}:`, error);
      }
    },
    [stompClient, currentUser?.id]
  );

  return { handleMessage, handleMessageStatusUpdate, sendMessage, sendMarkRead };
}
