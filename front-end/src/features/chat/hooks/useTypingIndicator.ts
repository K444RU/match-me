import { User } from '@/features/authentication';
import { useCallback, useEffect, useRef, useState } from 'react';
import { SET_TYPING_STATUS, TYPING_STATUS_SUBSCRIPTION } from '../graphql/typing.gql';
import { TypingStatusEvent } from '../types/TypingStatusEvent';
import { useAppMutation } from './useAppMutation';
import { useAppSubscription } from './useAppSubscription';

interface UseTypingIndicatorProps {
  currentUser: User;
}

export default function useTypingIndicator({ currentUser }: UseTypingIndicatorProps) {
  const [typingUsers, setTypingUsers] = useState<Record<string, boolean>>({});
  const typingTimeoutsRef = useRef<Record<string, NodeJS.Timeout>>({});
  const lastTypedRef = useRef<Record<string, number>>({});

  // Initialize the mutation
  const [setTypingStatus] = useAppMutation(SET_TYPING_STATUS);

  // Clear typing indicators after delay
  useEffect(() => {
    const timeouts = typingTimeoutsRef.current;
    return () => {
      Object.values(timeouts).forEach((timeout) => clearTimeout(timeout));
    };
  }, []);

  // Setup subscription to typing status updates
  useAppSubscription(TYPING_STATUS_SUBSCRIPTION, {
    onData: ({ data }) => {
      try {
        if (!data?.data?.typingStatusUpdates) return;

        const typingData = data.data.typingStatusUpdates as TypingStatusEvent;
        const senderId = String(typingData.senderId);

        // Clear existing timeout
        if (typingTimeoutsRef.current[senderId]) {
          clearTimeout(typingTimeoutsRef.current[senderId]);
        }

        // Update typing status
        setTypingUsers((prev) => ({ ...prev, [senderId]: typingData.isTyping }));

        // Set timeout to clear typing status
        if (typingData.isTyping) {
          typingTimeoutsRef.current[senderId] = setTimeout(() => {
            setTypingUsers((prev) => ({ ...prev, [senderId]: false }));
            delete typingTimeoutsRef.current[senderId];
          }, 5000); // Clear after 5 seconds
        }
      } catch (error) {
        console.error('Error handling typing status update:', error);
      }
    },
    skip: !currentUser?.id,
  });

  const sendTypingIndicator = useCallback(
    (connectionId: number) => {
      if (!currentUser?.id) {
        return;
      }

      // Throttle typing events - only send once per second per connection
      const now = Date.now();
      const connectionKey = String(connectionId);
      const lastTyped = lastTypedRef.current[connectionKey] || 0;

      if (now - lastTyped < 1000) {
        return; // Don't send if less than 1 second since last typing event
      }

      lastTypedRef.current[connectionKey] = now;

      // Validate connectionId
      const connectionIdNum = Number(connectionId);
      if (isNaN(connectionIdNum)) {
        console.error('Invalid connectionId for typing indicator:', connectionId);
        return;
      }

      try {
        setTypingStatus({
          variables: {
            input: {
              connectionId: String(connectionIdNum),
              senderId: String(currentUser.id),
              isTyping: true,
            },
          },
        });
      } catch (error) {
        console.error('Error sending typing indicator:', error);
      }
    },
    [currentUser, setTypingStatus]
  );

  return { typingUsers, sendTypingIndicator };
}
