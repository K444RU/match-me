import { User } from '@/features/authentication';
import { useEffect, useRef, useState } from 'react';
import { toast } from 'sonner';
import { ONLINE_STATUS_SUBSCRIPTION, PING } from '../graphql/online.gql';
import { useAppSubscription } from './useAppSubscription';
import { useAppQuery } from './useAppQuery';
import { OnlineStatusRequestDTO } from '../types';

interface UseOnlineIndicatorProps {
  currentUser: User;
}

const PING_INTERVAL_MS = 5000;

export default function useOnlineIndicator({ currentUser }: UseOnlineIndicatorProps) {
  const [onlineUsers, setOnlineUsers] = useState<Record<number, boolean>>({});
  const pingIntervalRef = useRef<NodeJS.Timeout | null>(null);

  const { refetch: pingServer } = useAppQuery(PING, {
    skip: true,
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: false,
    onError: (error) => {
        console.error('[useOnlineIndicator] Ping query failed:', error);
    },
    onCompleted: (data) => {
      const peerStatuses = data?.ping?.peerStatuses as OnlineStatusRequestDTO[] | undefined;
  if (peerStatuses) {
    setOnlineUsers((prev) => {
      const newState = { ...prev };
      let changed = false;
      peerStatuses.forEach((status) => {
        const peerUserId = Number(status.userId);
        if (newState[peerUserId] !== status.isOnline) {
          newState[peerUserId] = status.isOnline;
          changed = true;
        }
      });
      return changed ? newState : prev;
    });
  } else {
    console.warn('[useOnlineIndicator] No peerStatuses received in ping response.');
  }
    }
  });

  useAppSubscription(ONLINE_STATUS_SUBSCRIPTION, {
    skip: !currentUser?.id,
    onData: ({ data }) => {
      try {
        const update = data?.data?.onlineStatusUpdates;
        if (!update) {
          console.warn('[useOnlineIndicator] Received null/undefined onlineStatusUpdates');
          return;
        }

        const userId = Number(update.userId);
        const isOnline = update.isOnline;
        setOnlineUsers((prev) => {
          if (prev[userId] === isOnline) return prev;
          return { ...prev, [userId]: isOnline };
        });
      } catch (error) {
        console.error('Error handling online status update:', error);
      }
    },
    onError: (error) => {
      console.error('[useOnlineIndicator] onError callback triggered:', error);
      toast.error(`Subscription Error: ${error.message}`);
    },
  });

  useEffect(() => {
    if (currentUser?.id) {

      pingServer().catch((err) =>
        console.error('[useOnlineIndicator] Initial ping failed:', err),
      );

      if (pingIntervalRef.current) {
        clearInterval(pingIntervalRef.current);
      }
      pingIntervalRef.current = setInterval(() => {
        pingServer().catch((err) => {
          console.error('[useOnlineIndicator] Periodic ping failed:', err);
        });
      }, PING_INTERVAL_MS);


      return () => {
        if (pingIntervalRef.current) {
          clearInterval(pingIntervalRef.current);
          pingIntervalRef.current = null;
        }

      };
    } else {
      if (pingIntervalRef.current) {
        clearInterval(pingIntervalRef.current);
        pingIntervalRef.current = null;
      }
    }
  }, [currentUser?.id, pingServer]);
  return { onlineUsers };
}