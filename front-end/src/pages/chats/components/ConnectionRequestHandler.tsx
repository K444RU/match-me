import { useEffect, useState } from 'react';
import { useAuth } from '@features/authentication';
import { getConnections, acceptConnection, rejectConnection } from '@features/chat/connection-service.ts';
import { getUserController } from '@/api/user-controller.ts';

interface ConnectionProvider {
    connectionId: number;
    userId: number;
}

const ConnectionRequestHandler = () => {
    const { user } = useAuth();
    const [pendingIncomingIds, setPendingIncomingIds] = useState<ConnectionProvider[]>([]);
    const [userData, setUserData] = useState<{ [key: string]: any }>({});

    useEffect(() => {
        if (!user) return;

        (async () => {
            try {
                const data = await getConnections(user.token);
                console.log('Connections data:', data);
                const pending = data.pendingIncoming || [];
                setPendingIncomingIds(pending);

                const userPromises = pending.map((request: ConnectionProvider) =>
                    getUserController().getUser(request.userId, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    })
                );
                const userResponses = await Promise.all(userPromises);
                const users = userResponses.map((response) => response.data);

                const userMap = users.reduce((acc, user) => {
                    acc[user.id] = user;
                    return acc;
                }, {} as { [key: string]: any });

                setUserData(userMap);
            } catch (error) {
                console.error('Failed to fetch connections or user data:', error);
            }
        })();
    }, [user]);

    const handleAccept = async (connectionId: number) => {
        try {
            await acceptConnection(connectionId.toString(), user!.token);
            setPendingIncomingIds(pendingIncomingIds.filter((request) => request.connectionId !== connectionId));
        } catch (error) {
            console.error('Failed to accept connection:', error);
        }
    };

    const handleReject = async (connectionId: number) => {
        try {
            await rejectConnection(connectionId.toString(), user!.token);
            setPendingIncomingIds(pendingIncomingIds.filter((request) => request.connectionId !== connectionId));
        } catch (error) {
            console.error('Failed to reject connection:', error);
        }
    };

    return (
        <div className="space-y-2">
            <h3 className="text-sm font-semibold">Pending Incoming Requests</h3>
            {pendingIncomingIds.length === 0 ? (
                <p className="text-sm text-muted-foreground">No pending requests.</p>
            ) : (
                pendingIncomingIds.map((request) => {
                    const requester = userData[request.userId];
                    const displayName = requester
                        ? `${requester.firstName} ${requester.lastName} (${requester.alias})`
                        : 'Unknown User';

                    return (
                        <div key={request.connectionId} className="flex items-center justify-between">
                            <span className="text-sm">{displayName}</span>
                            <div className="space-x-2">
                                <button
                                    onClick={() => handleAccept(request.connectionId)}
                                    className="text-sm text-green-600 hover:underline"
                                >
                                    Accept
                                </button>
                                <button
                                    onClick={() => handleReject(request.connectionId)}
                                    className="text-sm text-red-600 hover:underline"
                                >
                                    Reject
                                </button>
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );
};

export default ConnectionRequestHandler;