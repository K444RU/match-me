import { getChatController } from '@/api/chat-controller';
import { ChatPreviewResponseDTO } from '@/api/types';
import { ChatPreview } from '@/types/api';

const chatController = getChatController();

export const chatService = {
  getChatPreviews: async () => {
    try {
      console.debug('🖖 ChatService: Making request');
      const token = localStorage.getItem('authToken');
      const response = await chatController.getChatPreviews({
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.debug('🖖 ChatService: Response', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Error fetching chat previews', error);
      throw error;
    }
  },

  sendMessage: (message: string, to: number, from: string) => {
    console.log(message, to, from);
  },

  mapDtoToChatPreview: (dto: ChatPreviewResponseDTO): ChatPreview => {
    return {
      connectionId: dto.connectionId ?? 0,
      participant: {
        alias: dto.connectedUserAlias ?? '',
        avatar: dto.connectedUserProfilePicture ?? '',
        firstName: dto.connectedUserFirstName ?? '',
        lastName: dto.connectedUserLastName ?? '',
      },
      lastMessage: {
        content: dto.lastMessageContent ?? '',
        sentAt: dto.lastMessageTimestamp ? Math.floor(new Date(dto.lastMessageTimestamp).getTime() / 1000) : 0,
      },
      unreadCount: dto.unreadMessageCount ?? 0,
    };
  },
};
