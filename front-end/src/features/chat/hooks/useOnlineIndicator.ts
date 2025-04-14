import { useCallback, useState } from 'react';
import { IMessage } from 'react-stomp-hooks';
import { OnlineStatusRequestDTO } from '../types';

export default function useOnlineIndicator() {
  const [onlineUsers, setOnlineUsers] = useState<Record<string, boolean>>({});

  const handleOnlineIndicator = useCallback((message: IMessage) => {
    try {
      const parsedBody = JSON.parse(message.body);

      if (Array.isArray(parsedBody)) {
        const data = parsedBody as OnlineStatusRequestDTO[];
        data.forEach((item) => {
          const userId = String(item.userId);
          setOnlineUsers((prev) => ({ ...prev, [userId]: item.isOnline }));
        });
      } else {
        const data = parsedBody as OnlineStatusRequestDTO;
        const userId = String(data.userId);
        setOnlineUsers((prev) => ({ ...prev, [userId]: data.isOnline }));
      }
    } catch (error) {
      // Update online status directly from backend
      console.error('Error parsing online indicator:', error, message.body);
    }
  }, []);

  return { onlineUsers, handleOnlineIndicator };
}
