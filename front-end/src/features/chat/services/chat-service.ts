import { ChatPreviewResponseDTO } from '@/api/types';
import { ChatPreview } from '@/types/api';

export const chatService = {
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
