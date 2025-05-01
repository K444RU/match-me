import { ChatMessageResponseDTO, MessagesSendRequestDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { useCallback } from 'react';
import {
  CHAT_MESSAGES_SUBSCRIPTION,
  MARK_MESSAGES_READ,
  MESSAGE_STATUS_SUBSCRIPTION,
  SEND_MESSAGE,
} from '../graphql/messages.gql';
import { MessageStatusUpdateDTO } from '../types/MessageStatusUpdateDTO';
import { useAppMutation } from './useAppMutation';
import { useAppSubscription } from './useAppSubscription';

interface UseMessageHandlerProps {
  currentUser: User;
  onMessageReceived: (message: ChatMessageResponseDTO) => void;
  onMessageStatusUpdateReceived: (message: MessageStatusUpdateDTO) => void;
}

export default function useMessageHandler({
  currentUser,
  onMessageReceived,
  onMessageStatusUpdateReceived,
}: UseMessageHandlerProps) {
  // Initialize mutations
  const [sendMessageMutation] = useAppMutation(SEND_MESSAGE);
  const [markReadMutation] = useAppMutation(MARK_MESSAGES_READ);

  // Setup subscription for new messages
  useAppSubscription(CHAT_MESSAGES_SUBSCRIPTION, {
    onData: ({ data }) => {
      try {
        // Directly access the single object based on the schema
        const message = data?.data?.messages;
        console.log(JSON.stringify(data.data, null, 2));
        if (message) {
          // Pass the single ChatMessage object to the callback
          message.messageId = Number(message.messageId);
          message.connectionId = Number(message.connectionId);
          message.senderId = Number(message.senderId);
          onMessageReceived(message);
        } else {
          console.warn('[useMessageHandler] Received null/undefined chatMessages');
        }
      } catch (error) {
        console.error('Error handling new message:', error);
      }
    },
    skip: !currentUser?.id,
  });

  useAppSubscription(MESSAGE_STATUS_SUBSCRIPTION, {
    onData: ({ data }) => {
      try {
        const messageStatusUpdate = data?.data?.messageStatus;
        console.log(JSON.stringify(data.data, null, 2));
        if (
          messageStatusUpdate &&
          messageStatusUpdate.messageId &&
          messageStatusUpdate.connectionId &&
          messageStatusUpdate.type
        ) {
          messageStatusUpdate.messageId = Number(messageStatusUpdate.messageId);
          messageStatusUpdate.connectionId = Number(messageStatusUpdate.connectionId);
          onMessageStatusUpdateReceived(messageStatusUpdate as MessageStatusUpdateDTO);
        } else {
          console.warn('[useMessageHandler] Received invalid message status update', messageStatusUpdate);
        }
      } catch (error) {
        console.error('Error handling message status update:', error);
      }
    },
    skip: !currentUser?.id,
  });

  const sendMessage = useCallback(
    async (message: MessagesSendRequestDTO): Promise<void> => {
      if (!currentUser?.id) {
        console.error('Cannot send message: User not authenticated');
        throw new Error('User not authenticated');
      }

      try {
        await sendMessageMutation({
          variables: {
            input: message,
          },
        });
      } catch (error) {
        console.error('Error sending message:', error);
        throw error;
      }
    },
    [currentUser, sendMessageMutation]
  );

  const sendMarkRead = useCallback(
    (connectionId: number) => {
      if (!currentUser?.id) {
        console.error('Cannot send markRead: No user ID.');
        return;
      }

      try {
        markReadMutation({
          variables: {
            input: {
              connectionId: String(connectionId),
            },
          },
        });
      } catch (error) {
        console.error(`Error sending markRead for connection ${connectionId}:`, error);
      }
    },
    [currentUser?.id, markReadMutation]
  );

  return { sendMessage, sendMarkRead, onMessageStatusUpdateReceived };
}
