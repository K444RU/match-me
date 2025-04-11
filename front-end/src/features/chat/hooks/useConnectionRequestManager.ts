import { useCallback, useState } from 'react';
import { useSubscription } from 'react-stomp-hooks';
import { Client } from 'react-stomp-hooks';
import { ConnectionUpdateMessage } from "@features/chat/types";

interface UseConnectionRequestManagerProps {
    userId: number | undefined;
    stompClient: Client | undefined;
}

export default function useConnectionRequestManager({ userId, stompClient }: UseConnectionRequestManagerProps) {
    const [connectionUpdates, setConnectionUpdates] = useState<ConnectionUpdateMessage[]>([]);

    // Subscribe to connection updates
    useSubscription(`/user/${userId}/queue/connectionUpdates`, (message) => {
        try {
            console.log(`[User ${userId}] Received RAW connection update:`, message.body);
            const update = JSON.parse(message.body);
            console.log(`[User ${userId}] Parsed connection update:`, update);
            setConnectionUpdates((prev) => [...prev, update]);
        } catch (e) {
            console.error("Failed to parse connection update", e, message.body);
        }
    });

    // Function to send a connection request
    const sendConnectionRequest = useCallback((targetUserId: number) => {
        if (stompClient?.connected) {
            stompClient.publish({
                destination: '/app/connection.sendRequest',
                body: JSON.stringify(targetUserId),
            });
        } else {
            console.error('STOMP client not connected, cannot send connection request.');
        }
    }, [stompClient]);

    // Function to accept a connection request
    const acceptConnectionRequest = useCallback((connectionId: number) => {
        if (stompClient?.connected) {
            stompClient.publish({
                destination: '/app/connection.acceptRequest',
                body: JSON.stringify(connectionId),
            });
        } else {
            console.error('STOMP client not connected, cannot accept connection request.');
        }
    }, [stompClient]);

    // Function to reject a connection request
    const rejectConnectionRequest = useCallback((connectionId: number) => {
        if (stompClient?.connected) {
            stompClient.publish({
                destination: '/app/connection.rejectRequest',
                body: JSON.stringify(connectionId),
            });
        } else {
            console.error('STOMP client not connected, cannot reject connection request.');
        }
    }, [stompClient]);

    // Function to disconnect a connection
    const disconnectConnection = useCallback((connectionId: number) => {
        if (stompClient?.connected) {
            stompClient.publish({
                destination: '/app/connection.disconnect',
                body: JSON.stringify(connectionId),
            });
        } else {
            console.error('STOMP client not connected, cannot disconnect connection.');
        }
    }, [stompClient]);

    return {
        connectionUpdates,
        sendConnectionRequest,
        acceptConnectionRequest,
        rejectConnectionRequest,
        disconnectConnection,
    };
}