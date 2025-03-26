import { useCallback, useState } from 'react';
import { IMessage } from 'react-stomp-hooks';
import { OnlineStatusRequestDTO } from '../types';

export default function useOnlineIndicator() {
  const [onlineUsers, setOnlineUsers] = useState<Record<string, boolean>>({});

  const handleOnlineIndicator = useCallback((message: IMessage) => {
    try {
      console.log('Online indicator received:', message.body);
      const data = JSON.parse(message.body) as OnlineStatusRequestDTO;

      // Validate data
      if (!data || typeof data !== 'object' || typeof data.isOnline !== 'boolean') {
        console.warn('Invalid online indicator format', message.body);
        return;
      }

      const senderId = String(data.connectionId);

      // Update online status directly from backend
      setOnlineUsers((prev) => ({ ...prev, [senderId]: data.isOnline }));
    } catch (error) {
      console.error('Error parsing online indicator:', error, message.body);
    }
  }, []);

  return { onlineUsers, handleOnlineIndicator };
}
