import { User } from '@/features/authentication';
import { useState } from 'react';
import { ONLINE_STATUS_SUBSCRIPTION } from '../graphql/online.gql';
import { useAppSubscription } from './useAppSubscription';

interface UseOnlineIndicatorProps {
  currentUser: User;
}

export default function useOnlineIndicator({ currentUser }: UseOnlineIndicatorProps) {
  const [onlineUsers, setOnlineUsers] = useState<Record<string, boolean>>({});

  // Setup subscription to online status updates
  useAppSubscription(ONLINE_STATUS_SUBSCRIPTION, {
    variables: { connectionId: currentUser?.id },
    onData: ({ data }) => {
      try {
        const update = data?.data?.onlineStatusUpdates;
        if (!update) {
          console.warn('[useOnlineIndicator] Received null/undefined onlineStatusUpdates');
          return;
        }

        const userId = String(update.userId);
        const isOnline = update.isOnline;
        setOnlineUsers((prev) => ({ ...prev, [userId]: isOnline }));
      } catch (error) {
        console.error('Error handling online status update:', error);
      }
    },
    skip: !currentUser?.id,
  });

  return { onlineUsers };
}
