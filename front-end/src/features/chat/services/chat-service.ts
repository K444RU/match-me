import { getChatController } from '@/api/chat-controller';
import {
  ChatPreviewResponseDTO,
  GetChatMessagesParams,
  MessagesSendRequestDTO,
  PageChatMessageResponseDTO,
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
      console.debug('🖖 ChatService: Making chat previews request');
      const response = await chatController.getChatPreviews();
      // DONT TOUCH THIS, THIS IS CORRECT, TYPE IS JUST FUCKING
      console.debug('🖖 ChatService: Response', response);
      return response;
    } catch (error) {
      console.error('❌ Error fetching chat previews', error);
      throw error;
    }
  },

  getChatMessages: async (connectionId: number): Promise<PageChatMessageResponseDTO> => {
    try {
      console.debug('🖖 ChatService: Making messages request');
      const response = await chatController.getChatMessages(connectionId, getChatMessagesParams);
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
      console.debug('🖖 ChatService: Sending message');
      const messageDto: MessagesSendRequestDTO = {
        content,
        connectionId,
      };

      await chatController.sendChatMessage(connectionId, messageDto);

      console.debug('🖖 ChatService: Message sent successfully');
    } catch (error) {
      console.error('❌ Error sending message:', error);
      throw error;
    }
  },
};
