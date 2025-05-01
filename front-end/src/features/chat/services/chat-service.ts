import { getChatController } from '@/api/chat-controller';
import {
  ChatPreviewResponseDTO,
  GetChatMessagesParams,
  MessagesSendRequestDTO,
  PageChatMessageResponseDTO,
} from '@/api/types';

const chatController = getChatController();

export const chatService = {
  getChatPreviews: async (): Promise<ChatPreviewResponseDTO[]> => {
    try {
      const response = await chatController.getChatPreviews();
      return response;
    } catch (error) {
      console.error('❌ Error fetching chat previews', error);
      throw error;
    }
  },

  getChatMessages: async (connectionId: number, params: GetChatMessagesParams): Promise<PageChatMessageResponseDTO> => {
    try {
      const response = await chatController.getChatMessages(connectionId, params);
      // DONT TOUCH THIS, THIS IS CORRECT, TYPE IS JUST FUCKING
      return response ?? [];
    } catch (error) {
      console.error('❌ Error fetching chat messages', error);
      throw error;
    }
  },

  sendMessage: async (messageDto: MessagesSendRequestDTO): Promise<void> => {
    try {
      await chatController.sendChatMessage(messageDto.connectionId, messageDto);
    } catch (error) {
      console.error('❌ Error sending message:', error);
      throw error;
    }
  },
};
