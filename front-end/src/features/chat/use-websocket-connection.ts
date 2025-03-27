import { useStompClient, useSubscription } from 'react-stomp-hooks';
import {ConnectionUpdateMessage} from "@features/chat/connectionUpdateMessage.ts";
import {useEffect, useState} from "react";

interface UseWebSocketConnectionProps {
	onConnectionChange: (connected: boolean) => void;
	onConnectionUpdate: (update: ConnectionUpdateMessage) => void;
}

export const useWebSocketConnection = ({ onConnectionChange, onConnectionUpdate }: UseWebSocketConnectionProps) => {
	const stompClient = useStompClient();

	const [isConnected, setIsConnected] = useState(false);

	useEffect(() => {
		if (stompClient) {
			console.log('STOMP client connected:', stompClient.connected);
			setIsConnected(stompClient.connected);
			onConnectionChange(stompClient.connected);
		} else {
			console.log('STOMP client not initialized');
			setIsConnected(false);
			onConnectionChange(false);
		}
	}, [stompClient, onConnectionChange]);

	useSubscription('/user/queue/connectionUpdates', (message) => {
		const update: ConnectionUpdateMessage = JSON.parse(message.body);
		console.log('Received connection update:', update);
		onConnectionUpdate(update);
	});

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
			console.log(`Sending connection request accepted ${connectionId}`);
			stompClient.publish({
				destination: '/app/connection.acceptRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('STOMP client not connected');
		}
	};

	const rejectConnectionRequest = (connectionId: number) => {
		if (stompClient) {
			stompClient.publish({
				destination: '/app/connection.rejectRequest',
				body: JSON.stringify(connectionId),
			});
		} else {
			console.error('STOMP client not connected');
		}
	};

	const disconnectConnection = (connectionId: number) => {
		if (stompClient) {
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