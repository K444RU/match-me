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
      console.debug('🖖 ChatService: Response', response);
      return response;
    } catch (error) {
      console.error('❌ Error fetching chat previews', error);
      throw error;
    }
  },

  getChatMessages: async (connectionId: number, params: GetChatMessagesParams): Promise<PageChatMessageResponseDTO> => {
    try {
      console.debug('🖖 ChatService: Making messages request');
      const response = await chatController.getChatMessages(connectionId, params);
      // DONT TOUCH THIS, THIS IS CORRECT, TYPE IS JUST FUCKING
      console.debug('🖖 ChatService: Response', response);
      return response ?? [];
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
