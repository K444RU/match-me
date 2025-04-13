import { getChatController } from '@/api/chat-controller';
import {
  ChatMessageResponseDTO,
  ChatPreviewResponseDTO,
  GetChatMessagesParams,
  MessagesSendRequestDTO,
} from '@/api/types';

const chatController = getChatController();

const getChatMessagesParams: GetChatMessagesParams = {
  pageable: {
    page: 0,
    size: 10,
  },
};

export const chatService = {
  getChatPreviews: async (): Promise<ChatPreviewResponseDTO[]> => {
    try {
      const response = await chatController.getChatPreviews();
      console.debug('🖖 ChatService: Response', response);
      return response;
    } catch (error) {
      console.error('❌ Error fetching chat previews', error);
      throw error;
    }
  },

  getChatMessages: async (connectionId: number): Promise<ChatMessageResponseDTO[]> => {
    try {
      const response = await chatController.getChatMessages(connectionId, getChatMessagesParams);
      console.debug('🖖 ChatService: Response', response.content);
      return response.content ?? [];
    } catch (error) {
      console.error('❌ Error fetching chat messages', error);
      throw error;
    }
  },

  sendMessage: async (content: string, connectionId: number): Promise<void> => {
    try {
      const messageDto: MessagesSendRequestDTO = {
        content,
        connectionId,
      };

      await chatController.sendChatMessage(connectionId, messageDto);

      console.debug('🖖 ChatService: Message sent successfully', messageDto);
    } catch (error) {
      console.error('❌ Error sending message:', error);
      throw error;
    }
  },
};
