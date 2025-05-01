import { ChatPreviewResponseDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { useState } from 'react';
import { CHAT_PREVIEWS_SUBSCRIPTION } from '../graphql/previews.gql';
import { useAppSubscription } from './useAppSubscription';

interface UseChatPreviewHandlerProps {
  currentUser: User;
}

export default function useChatPreviewHandler({ currentUser }: UseChatPreviewHandlerProps) {
  const [chatPreviews, setChatPreviews] = useState<ChatPreviewResponseDTO[]>([]);

  const handleChatPreviews = (previews: ChatPreviewResponseDTO[]) => {
    previews.forEach((preview) => {
      preview.connectionId = Number(preview.connectionId);
      preview.connectedUserId = Number(preview.connectedUserId);
    });

    // APPEND OR UPDATE existing previews instead of replacing them
    setChatPreviews((prevPreviews) => {
      const previewMap = new Map(prevPreviews.map((preview) => [preview.connectionId, preview]));

      // Update existing previews or add new ones
      previews.forEach((preview: ChatPreviewResponseDTO) => {
        previewMap.set(preview.connectionId, preview);
      });

      // Convert back to array
      const result = Array.from(previewMap.values());

      return result;
    });
  };

  // Subscribe to chat preview updates
  useAppSubscription(CHAT_PREVIEWS_SUBSCRIPTION, {
    skip: !currentUser?.id,
    onData: ({ data }) => {
      try {
        if (!data?.data?.chatPreviews) return;

        // Get the preview data from the subscription
        const newPreviews = Array.isArray(data.data.chatPreviews) ? data.data.chatPreviews : [data.data.chatPreviews];

        // Skip empty arrays or invalid data
        if (!newPreviews.length) {
          return;
        }

        // Filter valid previews
        const validPreviews = newPreviews.filter(
          (preview: ChatPreviewResponseDTO) => preview && typeof preview === 'object' && Number(preview.connectionId) > 0
        );

        if (!validPreviews.length) {
          return;
        }

        handleChatPreviews(validPreviews);
      } catch (error) {
        console.error('Error handling chat preview:', error);
      }
    },
  });

  return { chatPreviews, setChatPreviews, handleChatPreviews };
}
