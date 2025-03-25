import { ChatPreviewResponseDTO } from '@/api/types';
import { useCallback, useState } from 'react';
import { IMessage } from 'react-stomp-hooks';

export default function useChatPreviewHandler() {
  const [chatPreviews, setChatPreviews] = useState<ChatPreviewResponseDTO[]>([]);

  const handleChatPreviews = useCallback((message: IMessage) => {
    try {
      console.log('RECEIVED CHAT PREVIEW MESSAGE:', message.body.substring(0, 100) + '...');

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
        console.log('No valid previews in message');
        return;
      }

      // Filter valid previews
      const validPreviews = newPreviews.filter(
        (preview) => preview && typeof preview === 'object' && preview.connectionId > 0
      );

      console.log(`Processing ${validPreviews.length} valid previews out of ${newPreviews.length} total`);

      if (!validPreviews.length) {
        console.log('No valid previews after filtering');
        return;
      }

      // APPEND OR UPDATE existing previews instead of replacing them
      setChatPreviews((prevPreviews) => {
        console.log(`Merging ${validPreviews.length} new previews with ${prevPreviews.length} existing`);

        const previewMap = new Map(prevPreviews.map((preview) => [preview.connectionId, preview]));

        // Update existing previews or add new ones
        validPreviews.forEach((preview) => {
          previewMap.set(preview.connectionId, preview);
        });

        // Convert back to array
        const result = Array.from(previewMap.values());
        console.log(`Result: ${result.length} total chat previews after merge`);
        return result;
      });
    } catch (error) {
      console.error('Error handling chat preview:', error);
    }
  }, []);

  return { chatPreviews, setChatPreviews, handleChatPreviews };
}
