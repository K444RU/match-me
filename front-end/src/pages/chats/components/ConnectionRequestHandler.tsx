import { useEffect, useState } from 'react';
import { useAuth } from '@features/authentication';
import { useWebSocket } from '@/features/chat/websocket-context';
import { getUserController } from '@/api/user-controller';

interface ConnectionProvider {
    connectionId: number;
    userId: number;
}

const ConnectionRequestHandler = ({ pendingIncoming }: { pendingIncoming: ConnectionProvider[] }) => {
    const { user } = useAuth();
    const [pendingIncomingIds, setPendingIncomingIds] = useState<ConnectionProvider[]>(pendingIncoming);
    const [userData, setUserData] = useState<{ [key: string]: any }>({});
    const { acceptConnectionRequest, rejectConnectionRequest } = useWebSocket();

    useEffect(() => {
        console.log('[ConnectionRequestHandler] Updating local pendingIncoming state:', pendingIncoming);
        setPendingIncomingIds(pendingIncoming);
    }, [pendingIncoming]);

    useEffect(() => {
        if (!user || pendingIncomingIds.length === 0) return;
        (async () => {
            try {
                console.log('[ConnectionRequestHandler] Fetching user data for pending requests:', pendingIncomingIds);
                const userPromises = pendingIncomingIds.map((request) =>
                    getUserController().getUser(request.userId, {
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
                console.log('[ConnectionRequestHandler] Fetched user data:', userMap);
                setUserData(userMap);
            } catch (error) {
                console.error('[ConnectionRequestHandler] Failed to fetch user data:', error);
            }
        })();
    }, [pendingIncomingIds, user]);

    const handleAccept = async (connectionId: number) => {
        console.log('[ConnectionRequestHandler] Accepting connection:', connectionId);
        try {
            acceptConnectionRequest(connectionId);
            setPendingIncomingIds(pendingIncomingIds.filter((r) => r.connectionId !== connectionId));
        } catch (error) {
            console.error('[ConnectionRequestHandler] Failed to accept connection:', error);
        }
    };

    const handleReject = async (connectionId: number) => {
        console.log('[ConnectionRequestHandler] Rejecting connection:', connectionId);
        try {
            rejectConnectionRequest(connectionId);
            setPendingIncomingIds(pendingIncomingIds.filter((r) => r.connectionId !== connectionId));
        } catch (error) {
            console.error('[ConnectionRequestHandler] Failed to reject connection:', error);
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
                                <button onClick={() => handleAccept(request.connectionId)} className="text-sm text-green-600 hover:underline">
                                    Accept
                                </button>
                                <button onClick={() => handleReject(request.connectionId)} className="text-sm text-red-600 hover:underline">
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
