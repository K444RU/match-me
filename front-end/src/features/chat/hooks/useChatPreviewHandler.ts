import { ChatPreviewResponseDTO } from '@/api/types';
import { useCallback, useState } from 'react';
import { IMessage } from 'react-stomp-hooks';

export default function useChatPreviewHandler() {
  const [chatPreviews, setChatPreviews] = useState<ChatPreviewResponseDTO[]>([]);

  const handleChatPreviews = useCallback((message: IMessage) => {
    try {
      // Parse the message data
      let data;
      try {
        data = JSON.parse(message.body);
      } catch (error) {
        console.error('Failed to parse chat preview JSON:', error);
        return;
      }

      // Handle both array and single object formats
      const newPreviews = Array.isArray(data) ? data : [data];

      // Skip empty arrays or invalid data
      if (!newPreviews.length) {
        return;
      }

      // Filter valid previews
      const validPreviews = newPreviews.filter(
        (preview) => preview && typeof preview === 'object' && preview.connectionId > 0
      );

      if (!validPreviews.length) {
        return;
      }

      // APPEND OR UPDATE existing previews instead of replacing them
      setChatPreviews((prevPreviews) => {
        const previewMap = new Map(prevPreviews.map((preview) => [preview.connectionId, preview]));

        // Update existing previews or add new ones
        validPreviews.forEach((preview) => {
          previewMap.set(preview.connectionId, preview);
        });

        // Convert back to array
        const result = Array.from(previewMap.values());

        return result;
      });
    } catch (error) {
      console.error('Error handling chat preview:', error);
    }
  }, []);

  return { chatPreviews, setChatPreviews, handleChatPreviews };
}
