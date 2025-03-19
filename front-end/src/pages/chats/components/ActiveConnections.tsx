import { useEffect, useState } from 'react';
import { useAuth } from '@features/authentication';
import { getConnections, disconnectConnection } from '@features/chat/connection-service.ts';

const ActiveConnections = () => {
    const { user } = useAuth();
    const [activeConnections, setActiveConnections] = useState<any[]>([]);

    useEffect(() => {
        if (!user) return;

        (async () => {
            try {
                const data = await getConnections(user.token);
                setActiveConnections(data.active || []);
            } catch (error) {
                console.error('Failed to fetch connections:', error);
            }
        })();
    }, [user]);

    const handleDisconnect = async (connectionId: string) => {
        try {
            await disconnectConnection(connectionId, user.token);
            setActiveConnections(activeConnections.filter((conn) => conn.id !== connectionId));
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
                activeConnections.map((connection) => (
                    <div key={connection.id} className="flex items-center justify-between">
                        <span className="text-sm">{connection.userId}</span>
                        <button
                            onClick={() => handleDisconnect(connection.id)}
                            className="text-sm text-red-600 hover:underline"
                        >
                            Disconnect
                        </button>
                    </div>
                ))
            )}
        </div>
    );
};

export default ActiveConnections;