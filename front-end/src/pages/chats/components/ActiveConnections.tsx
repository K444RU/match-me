import { useEffect, useState } from 'react';
import { useAuth } from '@features/authentication';
import { useWebSocket } from '@/features/chat/websocket-context';
import { getUserController } from '@/api/user-controller.ts';

interface ConnectionProvider {
    connectionId: number;
    userId: number;
}

const ActiveConnections = ({ active }: { active: ConnectionProvider[] }) => {
    const { user } = useAuth();
    const [activeConnections, setActiveConnections] = useState<ConnectionProvider[]>(active);
    const [userData, setUserData] = useState<{ [key: string]: any }>({});
    const { disconnectConnection } = useWebSocket();

    useEffect(() => {
        setActiveConnections(active);
    }, [active]);

    useEffect(() => {
        if (!user || activeConnections.length === 0) return;

        (async () => {
            try {
                const userPromises = activeConnections.map((connection) =>
                    getUserController().getUser(connection.userId, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    })
                );
                const userResponses = await Promise.all(userPromises);
                const users = userResponses.map((response) => response.data);

                const userMap = users.reduce((acc, user) => {
                    // @ts-ignore
                    acc[user.id] = user;
                    return acc;
                }, {} as { [key: string]: any });

                setUserData(userMap);
            } catch (error) {
                console.error('Failed to fetch user data:', error);
            }
        })();
    }, [activeConnections, user]);

    const handleDisconnect = async (connectionId: number) => {
        try {
            disconnectConnection(connectionId);
            setActiveConnections(activeConnections.filter((connection) => connection.connectionId !== connectionId));
        } catch (error) {
            console.error('Failed to disconnect:', error);
        }
    };

    return (
        <div className="space-y-2">
            <h3 className="text-sm font-semibold">Active Connections</h3>
            {activeConnections.length === 0 ? (
                <p className="text-sm text-muted-foreground">No active connections.</p>
            ) : (
                activeConnections.map((connection) => {
                    const connectedUser = userData[connection.userId];
                    const displayName = connectedUser
                        ? `${connectedUser.firstName} ${connectedUser.lastName} (${connectedUser.alias})`
                        : 'Unknown User';

                    return (
                        <div key={connection.connectionId} className="flex items-center justify-between">
                            <span className="text-sm">{displayName}</span>
                            <button
                                onClick={() => handleDisconnect(connection.connectionId)}
                                className="text-sm text-red-600 hover:underline"
                            >
                                Disconnect
                            </button>
                        </div>
                    );
                })
            )}
        </div>
    );
};

export default ActiveConnections;