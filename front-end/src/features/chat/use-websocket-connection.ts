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
			setIsConnected(stompClient.connected);
			onConnectionChange(stompClient.connected);
		} else {
			setIsConnected(false);
			onConnectionChange(false);
		}
	}, [stompClient, onConnectionChange]);

	const sendConnectionRequest = (targetUserId: number) => {
		if (stompClient && stompClient.connected) {
			console.log(`Sending connection request to user ${targetUserId}`);
			stompClient.publish({
				destination: '/app/connection.sendRequest',
				body: JSON.stringify(targetUserId),
			});
		} else {
			console.error('STOMP client not connected');
		}
	};

	const acceptConnectionRequest = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			stompClient.publish({
				destination: '/app/connection.acceptRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('STOMP client not connected');
		}
	};

	const rejectConnectionRequest = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			stompClient.publish({
				destination: '/app/connection.rejectRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('STOMP client not connected');
		}
	};

	const disconnectConnection = (connectionId: number) => {
		if (stompClient && stompClient.connected) {
			stompClient.publish({
				destination: '/app/connection.disconnect',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('STOMP client not connected');
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
