import { useEffect, useState } from 'react';
import { useStompClient } from 'react-stomp-hooks';

interface UseWebSocketConnectionProps {
	onConnectionChange: (connected: boolean) => void;
}

export const useWebSocketConnection = ({ onConnectionChange }: UseWebSocketConnectionProps) => {
	const stompClient = useStompClient();
	const [isConnected, setIsConnected] = useState(false);

	useEffect(() => {
		if (stompClient) {
			console.log('[useWebSocketConnection] STOMP client available, connected:', stompClient.connected);
			setIsConnected(stompClient.connected);
			onConnectionChange(stompClient.connected);
		} else {
			console.log('[useWebSocketConnection] STOMP client not available');
			setIsConnected(false);
			onConnectionChange(false);
		}
	}, [stompClient, onConnectionChange]);

	const sendConnectionRequest = (targetUserId: number) => {
		if (stompClient && stompClient.connected) {
			console.log(`[useWebSocketConnection] Sending connection request to user ${targetUserId}`);
			stompClient.publish({
				destination: '/app/connection.sendRequest',
				body: JSON.stringify(targetUserId),
			});
		} else {
			console.error('[useWebSocketConnection] Cannot send connection request: STOMP client not connected');
		}
	};

	const acceptConnectionRequest = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			console.log(`[useWebSocketConnection] Sending accept request for connection ${connectionId}`);
			stompClient.publish({
				destination: '/app/connection.acceptRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('[useWebSocketConnection] Cannot send accept request: STOMP client not connected');
		}
	};

	const rejectConnectionRequest = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			console.log(`[useWebSocketConnection] Sending reject request for connection ${connectionId}`);
			stompClient.publish({
				destination: '/app/connection.rejectRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('[useWebSocketConnection] Cannot send reject request: STOMP client not connected');
		}
	};

	const disconnectConnection = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			console.log(`[useWebSocketConnection] Sending disconnect for connection ${connectionId}`);
			stompClient.publish({
				destination: '/app/connection.disconnect',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('[useWebSocketConnection] Cannot send disconnect: STOMP client not connected');
		}
	};

	return {
		isConnected,
		sendConnectionRequest,
		acceptConnectionRequest,
		rejectConnectionRequest,
		disconnectConnection,
	};
};
