import { useEffect, useState } from 'react';
import { useAuth } from '@features/authentication';
import { getConnections, acceptConnection, rejectConnection } from '@features/chat/connection-service.ts';

const ConnectionRequestHandler = () => {
    const { user } = useAuth();
    const [pendingIncoming, setPendingIncoming] = useState<any[]>([]);

    useEffect(() => {
        if (!user) return;

        (async () => {
            try {
                const data = await getConnections(user.token);
                setPendingIncoming(data.pendingIncoming || []);
            } catch (error) {
                console.error('Failed to fetch connections:', error);
            }
        })();
    }, [user]);

    const handleAccept = async (requestId: string) => {
        try {
            await acceptConnection(requestId, user.token);
            setPendingIncoming(pendingIncoming.filter((req) => req.id !== requestId));
        } catch (error) {
            console.error('Failed to accept connection:', error);
        }
    };

    const handleReject = async (requestId: string) => {
        try {
            await rejectConnection(requestId, user.token);
            setPendingIncoming(pendingIncoming.filter((req) => req.id !== requestId));
        } catch (error) {
            console.error('Failed to reject connection:', error);
        }
    };

    return (
        <div className="space-y-2">
            <h3 className="text-sm font-semibold">Pending Incoming Requests</h3>
            {pendingIncoming.length === 0 ? (
                <p className="text-sm text-muted-foreground">No pending requests.</p>
            ) : (
                pendingIncoming.map((request) => (
                    <div key={request.id} className="flex items-center justify-between">
                        <span className="text-sm">{request.userId}</span>
                        <div className="space-x-2">
                            <button
                                onClick={() => handleAccept(request.id)}
                                className="text-sm text-green-600 hover:underline"
                            >
                                Accept
                            </button>
                            <button
                                onClick={() => handleReject(request.id)}
                                className="text-sm text-red-600 hover:underline"
                            >
                                Reject
                            </button>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
};

export default ConnectionRequestHandler;