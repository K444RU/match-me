import { User } from '@/features/authentication';
import { useCallback, useEffect, useRef, useState } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';
import { TypingStatusRequestDTO } from '../types';

interface UseTypingIndicatorProps {
  stompClient: Client | undefined;
  currentUser: User;
}

export default function useTypingIndicator({ stompClient, currentUser }: UseTypingIndicatorProps) {
  const [typingUsers, setTypingUsers] = useState<Record<string, boolean>>({});
  const typingTimeoutsRef = useRef<Record<string, NodeJS.Timeout>>({});
  const lastTypedRef = useRef<Record<string, number>>({});

  // Clear typing indicators after delay
  useEffect(() => {
    return () => {
      // Clean up any timeouts on unmount
      Object.values(typingTimeoutsRef.current).forEach((timeout) => clearTimeout(timeout));
    };
  }, []);

  const handleTypingIndicator = useCallback((message: IMessage) => {
    try {
      console.log('Typing indicator received:', message.body);
      const data = JSON.parse(message.body) as TypingStatusRequestDTO;

      // Validate data
      if (!data || typeof data !== 'object' || typeof data.isTyping !== 'boolean') {
        console.warn('Invalid typing indicator format', message.body);
        return;
      }

      const senderId = String(data.senderId);

      // Clear existing timeout
      if (typingTimeoutsRef.current[senderId]) {
        clearTimeout(typingTimeoutsRef.current[senderId]);
      }

      // Update typing status
      setTypingUsers((prev) => ({ ...prev, [senderId]: data.isTyping }));

      // Set timeout to clear typing status
      if (data.isTyping) {
        typingTimeoutsRef.current[senderId] = setTimeout(() => {
          setTypingUsers((prev) => ({ ...prev, [senderId]: false }));
          delete typingTimeoutsRef.current[senderId];
        }, 5000); // Clear after 5 seconds
      }
    } catch (error) {
      console.error('Error parsing typing indicator:', error, message.body);
    }
  }, []);

  const sendTypingIndicator = useCallback(
    (connectionId: number) => {
      if (!stompClient?.connected || !currentUser?.id) {
        console.log('Cannot send typing indicator: WebSocket not connected or user not available');
        return;
      }
      console.log('Sending typing indicator:', connectionId);
      // Throttle typing events - only send once per second per connection
      const now = Date.now();
      const connectionKey = String(connectionId);
      const lastTyped = lastTypedRef.current[connectionKey] || 0;

      if (now - lastTyped < 1000) {
        return; // Don't send if less than 1 second since last typing event
      }

      lastTypedRef.current[connectionKey] = now;

      // Parse connectionId
      const connectionIdNum = Number(connectionId);
      if (isNaN(connectionIdNum)) {
        console.error('Invalid connectionId for typing indicator:', connectionId);
        return;
      }

      const typingData = {
        connectionId: connectionIdNum,
        senderId: currentUser.id,
        isTyping: true,
      };

      try {
        console.log('Sending typing indicator:', typingData);
        stompClient.publish({
          destination: '/app/chat.typing',
          body: JSON.stringify(typingData),
          headers: {
            'content-type': 'application/json',
          },
        });
      } catch (error) {
        console.error('Error sending typing indicator:', error);
      }
    },
    [stompClient, currentUser]
  );

  return { typingUsers, handleTypingIndicator, sendTypingIndicator };
}
