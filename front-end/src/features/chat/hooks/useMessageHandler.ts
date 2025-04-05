import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { MutableRefObject, useCallback } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';

interface UseMessageHandlerProps {
  stompClientRef: MutableRefObject<Client | undefined>;
  currentUser: User;
  onMessageReceived: (message: ChatMessageResponseDTO) => void;
  onMessageStatusUpdateReceived: (message: MessageStatusUpdateDTO) => void;
}

export default function useMessageHandler({
  stompClientRef,
  currentUser,
  onMessageReceived,
  onMessageStatusUpdateReceived,
}: UseMessageHandlerProps) {
  const handleMessage = useCallback(
    (message: IMessage) => {
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
    },
    [onMessageReceived]
  );

  const handleMessageStatusUpdate = useCallback(
    (message: IMessage) => {
      try {
        const parsedData = JSON.parse(message.body);

        // Handle potential array format
        const dataArray = Array.isArray(parsedData) ? parsedData : [parsedData];

        for (const data of dataArray) {
          if (!data || typeof data !== 'object' || !data.messageId || !data.connectionId || !data.type) {
            console.warn('Received invalid message status object format', data);
            continue; // Skip this invalid object
          }
          onMessageStatusUpdateReceived(data as MessageStatusUpdateDTO);
        }
      } catch (error) {
        console.error('Error parsing message status:', error, message.body);
      }
    },
    [onMessageStatusUpdateReceived]
  );

  const sendMessage = useCallback(
    async (message: MessagesSendRequestDTO): Promise<void> => {
      if (!stompClientRef.current?.connected) {
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

          stompClientRef.current?.publish({
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
    [stompClientRef, currentUser]
  );

  const sendMarkRead = useCallback(
    (connectionId: number) => {
      if (!stompClientRef.current?.connected || !currentUser?.id) {
        console.error('Cannot send markRead: STOMP client not connected or no user ID.');
        return;
      }
      try {
        stompClientRef.current?.publish({
          destination: '/app/chat.markRead',
          body: JSON.stringify({ connectionId: connectionId.toString() }),
        });
      } catch (error) {
        console.error(`Error sending markRead for connection ${connectionId}:`, error);
      }
    },
    [stompClientRef, currentUser?.id]
  );

  return { handleMessage, handleMessageStatusUpdate, sendMessage, sendMarkRead };
}
