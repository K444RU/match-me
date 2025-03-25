import { useCallback, useEffect, useRef } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';

interface UseSubscriptionManagerProps {
  userId: number | undefined;
  stompClient: Client | undefined;
  handleMessage: (message: IMessage) => void;
  handleTypingIndicator: (message: IMessage) => void;
  handleChatPreviews: (message: IMessage) => void;
}

export default function useSubscriptionManager({
  userId,
  stompClient,
  handleMessage,
  handleTypingIndicator,
  handleChatPreviews,
}: UseSubscriptionManagerProps) {
  const subscriptionsRef = useRef<{ [key: string]: { unsubscribe: () => void } }>({});
  const stompClientRef = useRef<Client | null>(null);
  const isSubscribedRef = useRef<boolean>(false);
  const lastPingRef = useRef<number>(0);
  const pingIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const handlersRef = useRef({
    handleMessage,
    handleTypingIndicator,
    handleChatPreviews,
  });

  // Keep handlers up to date
  useEffect(() => {
    handlersRef.current = {
      handleMessage,
      handleTypingIndicator,
      handleChatPreviews,
    };
  }, [handleMessage, handleTypingIndicator, handleChatPreviews]);

  // Update client reference
  useEffect(() => {
    if (stompClient) {
      stompClientRef.current = stompClient;
    }
  }, [stompClient]);

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
  }, [userId]);

  // Setup subscriptions
  const setupSubscriptions = useCallback(() => {
    if (!stompClientRef.current || !userId) {
      console.error('Cannot setup subscriptions: No STOMP client or userId available');
      return;
    }

    // Don't re-subscribe if already subscribed
    if (isSubscribedRef.current && Object.keys(subscriptionsRef.current).length > 0) {
      console.log('Subscriptions already set up, skipping');
      return;
    }

    // Clear any existing subscriptions
    clearSubscriptions();

    try {
      console.log('Setting up WebSocket subscriptions for user:', userId);

      // Subscribe to channels
      subscriptionsRef.current.pong = stompClientRef.current.subscribe(
        `/user/${userId}/queue/pong`,
        (message: IMessage) => {
          console.log('PONG RECEIVED:', message.body);
          lastPingRef.current = Date.now();
        }
      );

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
  }, [userId, sendPing, clearSubscriptions]);

  // Reconnection function
  const reconnect = useCallback(() => {
    clearSubscriptions();

    if (stompClientRef.current?.connected && userId) {
      setupSubscriptions();
    }
  }, [userId, setupSubscriptions, clearSubscriptions]);

  // Setup subscriptions when client or userId changes
  useEffect(() => {
    if (stompClient?.connected && userId) {
      setupSubscriptions();
    }
  }, [stompClient?.connected, userId, setupSubscriptions]);

  return { setupSubscriptions, reconnect };
}
