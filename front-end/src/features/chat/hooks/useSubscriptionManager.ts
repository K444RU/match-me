import { MutableRefObject, useCallback, useEffect, useRef, useState } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';

interface UseSubscriptionManagerProps {
  userId: number | undefined;
  stompClientRef: MutableRefObject<Client | undefined>;
  stompClientConnected: boolean;
  handleMessage: (message: IMessage) => void;
  handleMessageStatusUpdate: (message: IMessage) => void;
  handleTypingIndicator: (message: IMessage) => void;
  handleChatPreviews: (message: IMessage) => void;
  handleOnlineIndicator: (message: IMessage) => void;
}

export default function useSubscriptionManager({
  userId,
  stompClientRef,
  stompClientConnected,
  handleMessage,
  handleMessageStatusUpdate,
  handleTypingIndicator,
  handleChatPreviews,
  handleOnlineIndicator,
}: UseSubscriptionManagerProps) {
  const subscriptionsRef = useRef<{ [key: string]: { unsubscribe: () => void } }>({});
  const isSubscribedRef = useRef<boolean>(false);
  const lastPingRef = useRef<number>(0);
  const pingIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const lastConnectionChangeRef = useRef<number>(Date.now());
  const handlersRef = useRef({
    handleMessage,
    handleMessageStatusUpdate,
    handleTypingIndicator,
    handleChatPreviews,
    handleOnlineIndicator,
  });

  // Keep handlers up to date
  useEffect(() => {
    handlersRef.current = {
      handleMessage,
      handleMessageStatusUpdate,
      handleTypingIndicator,
      handleChatPreviews,
      handleOnlineIndicator,
    };
  }, [handleMessage, handleMessageStatusUpdate, handleTypingIndicator, handleChatPreviews, handleOnlineIndicator]);

  // Manage stable connection state with debouncing
  useEffect(() => {
    if (!stompClientRef.current) return;

    const now = Date.now();

    if (stompClientConnected !== isConnected) {
      lastConnectionChangeRef.current = now;
    }

    // handle disconnect immediately
    if (!stompClientConnected && isConnected) {
      setIsConnected(false);
      return;
    }

    // add debounce for connections
    if (stompClientConnected && !isConnected) {
      const debounceTimer = setTimeout(() => {
        if (stompClientRef.current?.connected) {
          setIsConnected(true);
        }
      }, 1000);

      return () => clearTimeout(debounceTimer);
    }
  }, [stompClientConnected, isConnected, stompClientRef]);

  // Clear subscriptions helper
  const clearSubscriptions = useCallback(() => {
    Object.entries(subscriptionsRef.current).forEach(([key, sub]) => {
      try {
        sub.unsubscribe();
        delete subscriptionsRef.current[key];
      } catch (e) {
        console.error(`Error unsubscribing from ${key}:`, e);
      }
    });

    isSubscribedRef.current = false;
  }, []);

  // Clean up on unmount
  useEffect(() => {
    return () => {
      if (pingIntervalRef.current) {
        clearInterval(pingIntervalRef.current);
        pingIntervalRef.current = null;
      }

      // Clean up any subscriptions
      clearSubscriptions();
    };
  }, [clearSubscriptions]);

  // Send ping function - defined outside of setupSubscriptions
  const sendPing = useCallback(() => {
    if (!stompClientRef.current || !userId) return;

    try {
      stompClientRef.current.publish({
        destination: '/app/chat.ping',
        body: JSON.stringify({
          timestamp: new Date().toISOString(),
          userId,
        }),
      });
      lastPingRef.current = Date.now();
    } catch (error) {
      console.error('Error sending ping:', error);
    }
  }, [userId, stompClientRef]);

  // Setup subscriptions
  const setupSubscriptions = useCallback(() => {
    if (!stompClientRef.current || !userId) {
      console.error('Cannot setup subscriptions: No STOMP client or userId available');
      return;
    }

    // Don't re-subscribe if already subscribed
    if (isSubscribedRef.current && Object.keys(subscriptionsRef.current).length > 0) {
      return;
    }

    // Clear any existing subscriptions
    clearSubscriptions();

    try {
      // Subscribe to channels
      subscriptionsRef.current.pong = stompClientRef.current.subscribe(`/user/${userId}/queue/pong`, () => {
        lastPingRef.current = Date.now();
      });

      subscriptionsRef.current.messages = stompClientRef.current.subscribe(
        `/user/${userId}/queue/messages`,
        (message) => handlersRef.current.handleMessage(message)
      );

      subscriptionsRef.current.typing = stompClientRef.current.subscribe(`/user/${userId}/queue/typing`, (message) =>
        handlersRef.current.handleTypingIndicator(message)
      );

      subscriptionsRef.current.previews = stompClientRef.current.subscribe(
        `/user/${userId}/queue/previews`,
        (message) => handlersRef.current.handleChatPreviews(message)
      );

      subscriptionsRef.current.online = stompClientRef.current.subscribe(`/user/${userId}/queue/online`, (message) => {
        handlersRef.current.handleOnlineIndicator(message);
      });

      subscriptionsRef.current.messageStatus = stompClientRef.current.subscribe(
        `/user/${userId}/queue/messageStatus`,
        (message) => {
          handlersRef.current.handleMessageStatusUpdate(message);
        }
      );

      // Set up a ping interval
      if (pingIntervalRef.current) {
        clearInterval(pingIntervalRef.current);
      }

      pingIntervalRef.current = setInterval(sendPing, 30000);

      // Send initial ping
      sendPing();

      isSubscribedRef.current = true;
    } catch (error) {
      console.error('Error setting up subscriptions:', error);
      isSubscribedRef.current = false;
    }
  }, [userId, sendPing, clearSubscriptions, stompClientRef]);

  // Reconnection function
  const reconnect = useCallback(() => {
    const timeSinceLastChange = Date.now() - lastConnectionChangeRef.current;

    if (timeSinceLastChange < 2000) {
      return;
    }

    // clear subscriptions if disconnected
    if (!stompClientRef.current?.connected) {
      clearSubscriptions();
    }

    if (stompClientRef.current && userId) {
      if (!stompClientRef.current.connected) {
        try {
          stompClientRef.current.activate();
        } catch (error) {
          console.error('Error activating STOMP client: ', error);
        }
      } else {
        setupSubscriptions();
      }
    }
  }, [userId, setupSubscriptions, clearSubscriptions, stompClientRef]);

  // Setup subscriptions when client or userId changes
  useEffect(() => {
    if (stompClientRef.current?.connected && userId) {
      setupSubscriptions();
    }
  }, [isConnected, userId, setupSubscriptions, stompClientRef]);

  return { setupSubscriptions, reconnect };
}
