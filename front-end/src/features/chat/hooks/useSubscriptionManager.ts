import { useCallback, useEffect, useRef, useState } from 'react';
import { Client, IMessage } from 'react-stomp-hooks';

interface UseSubscriptionManagerProps {
  userId: number | undefined;
  stompClient: Client | undefined;
  handleMessage: (message: IMessage) => void;
  handleTypingIndicator: (message: IMessage) => void;
  handleChatPreviews: (message: IMessage) => void;
  handleOnlineIndicator: (message: IMessage) => void;
}

export default function useSubscriptionManager({
  userId,
  stompClient,
  handleMessage,
  handleTypingIndicator,
  handleChatPreviews,
  handleOnlineIndicator,
}: UseSubscriptionManagerProps) {
  const subscriptionsRef = useRef<{ [key: string]: { unsubscribe: () => void } }>({});
  const stompClientRef = useRef<Client | null>(null);
  const isSubscribedRef = useRef<boolean>(false);
  const lastPingRef = useRef<number>(0);
  const pingIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const lastConnectionChangeRef = useRef<number>(Date.now());
  const handlersRef = useRef({
    handleMessage,
    handleTypingIndicator,
    handleChatPreviews,
    handleOnlineIndicator,
  });

  // Keep handlers up to date
  useEffect(() => {
    handlersRef.current = {
      handleMessage,
      handleTypingIndicator,
      handleChatPreviews,
      handleOnlineIndicator,
    };
  }, [handleMessage, handleTypingIndicator, handleChatPreviews, handleOnlineIndicator]);

  // Update client reference
  useEffect(() => {
    if (stompClient) {
      stompClientRef.current = stompClient;
    }
  }, [stompClient]);

  // Manage stable connection state with debouncing
  useEffect(() => {
    if (!stompClient) return;

    const currentConnected = !!stompClient.connected;
    const now = Date.now();

    if (currentConnected !== isConnected) {
      lastConnectionChangeRef.current = now;
    }

    // handle disconnect immediately
    if (!currentConnected && isConnected) {
      setIsConnected(false);
      return;
    }

    // add debounce for connections
    if (currentConnected && !isConnected) {
      const debounceTimer = setTimeout(() => {
        if (stompClient.connected) {
          setIsConnected(true);
        }
      }, 1000);

      return () => clearTimeout(debounceTimer);
    }
  }, [stompClient?.connected, isConnected]);

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

      subscriptionsRef.current.online = stompClientRef.current.subscribe(`/user/${userId}/queue/online`, (message) => {
        handlersRef.current.handleOnlineIndicator(message);
        console.log('ONLINE RECEIVED:', message.body);
      });

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
    const timeSinceLastChange = Date.now() - lastConnectionChangeRef.current;

    if (timeSinceLastChange < 2000) {
      console.log('Connection state recently changed, avoiding reconnect');
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
  }, [userId, setupSubscriptions, clearSubscriptions]);

  // Setup subscriptions when client or userId changes
  useEffect(() => {
    if (stompClient?.connected && userId) {
      setupSubscriptions();
    }
  }, [isConnected, userId, setupSubscriptions]);

  return { setupSubscriptions, reconnect };
}
