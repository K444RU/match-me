import { MessagesSendRequestDTOWithSender } from "@/api/types";
import { useCallback, useEffect, useRef, useState } from "react";
import { IMessage, useStompClient, useSubscription } from "react-stomp-hooks";


const TYPING_TIMEOUT = 5000;
const MAX_RECONNECTION_ATTEMPTS = 3;
const RECONNECTION_DELAY = 2000;

interface UseWebSocketConnectionProps {
  	onMessage: (message: MessagesSendRequestDTOWithSender) => void;
  	onTypingIndicator: (userId: string, isTyping: boolean) => void;
  	onOnlineIndicator: (userId: string, isOnline: boolean) => void;
  	onConnectionChange: (connected: boolean) => void;
}

export const useWebSocketConnection = ({
  	onMessage,
  	onTypingIndicator,
  	onOnlineIndicator,
  	onConnectionChange
}: UseWebSocketConnectionProps) => {
	const stompClient = useStompClient();
	const [isConnected, setIsConnected] = useState(false);
	const typingTimeout = useRef<ReturnType<typeof setTimeout>>();
	const reconnectionAttempts = useRef(0);

  const handleConnectionChange = useCallback((connected: boolean) => {
      setIsConnected(connected);
      onConnectionChange?.(connected);
      if (connected) {
        reconnectionAttempts.current = 0;
      }
    }, [onConnectionChange]);

  const handleReconnection = useCallback(() => {
    if (reconnectionAttempts.current >= MAX_RECONNECTION_ATTEMPTS) {
      console.error('Reconnection attempts reached, timed out.')
      return;
    }
    setTimeout(() => {
      reconnectionAttempts.current++;
      stompClient?.activate();
    }, RECONNECTION_DELAY * Math.pow(2, reconnectionAttempts.current))
  }, [stompClient]);

  const safeParseJSON = <T,>(json: string, errorMsg: string): T | null => {
    try {
      return JSON.parse(json) as T;
    } catch (error) {
      console.error(errorMsg, error);
      return null;
    }
  }

	useEffect(() => {
		if (!stompClient) return;

		const onConnect = () => {
			handleConnectionChange(true);
		};

    const onDisconnect = () => {
			handleConnectionChange(false);
			handleReconnection();
		};

		stompClient.onConnect = onConnect;
		stompClient.onDisconnect = onDisconnect;

		return () => {
      stompClient.onConnect = () => {};
      stompClient.onDisconnect = () => {};
      handleConnectionChange(false);
    };

	}, [stompClient, handleConnectionChange, handleReconnection])

  // Message subscription with error handling and retry logic
  const handleIncomingMessage = (message: IMessage) => {
    const parsed = safeParseJSON<MessagesSendRequestDTOWithSender>(
      message.body,
      'Error processing message:'
    );
    if (parsed) onMessage(parsed);
  }

	// Typing indicator subscription
  const handleTypingMessage = (message: IMessage) => {
    const parsed = safeParseJSON<{ userId: string; isTyping: boolean }>(
      message.body,
      'Error processing typing indicator:'
    );
    if (parsed) onTypingIndicator(parsed.userId, parsed.isTyping)
  }

  useSubscription(`/user/queue/messages`, handleIncomingMessage);
  useSubscription(`/user/queue/typing`, handleTypingMessage);


	/**
	 * Sends a message to the user specific user via Websocket
	 * @param userId - The users ID to which the message will be sent
	 * @param message - The message which will be sent to the user via Websocket
	 */
	const sendMessage = useCallback(async (message: MessagesSendRequestDTOWithSender): Promise<void> => {

	}, [stompClient]);

	/**
	 * Sends an indicator to the other user that this user is typing. Has a magic number to manage typing timeout.
	 * @param userId - The users ID to which the indicator will be sent
	 */
	const sendTypingIndicator = useCallback((userId: string) => {
		if(!stompClient?.connected) return;
	
		if (typingTimeout.current) {
		  	clearTimeout(typingTimeout.current);
		}
	}, [stompClient]);

	const sendOnlineIndicator = useCallback((userId: string) => {

	}, [stompClient]);

	return {
		isConnected,
		sendMessage,
		sendTypingIndicator,
		sendOnlineIndicator,
	}
}
