import {CurrentUserResponseDTO} from '@/api/types';
import {userService} from '@/features/user';
import {useAuth} from '@features/authentication';
import {getConnections, useCommunication} from '@features/chat';
import {useCallback, useEffect, useState} from 'react';

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

export default function ConnectionRequestHandler() {
  const { user } = useAuth();
  const { connectionUpdates, acceptConnectionRequest, rejectConnectionRequest } = useCommunication();
  const [pendingIncomingIds, setPendingIncomingIds] = useState<ConnectionProvider[]>([]);
  const [userData, setUserData] = useState<{ [key: string]: CurrentUserResponseDTO }>({});

  const fetchUserData = useCallback(async (userId: number) => {
    if (userData[userId]) {
      return;
    }
    try {
      console.log(`Workspace user data for ID: ${userId}`);
      const fetchedUser = await userService.getUser(userId);
      if (fetchedUser) {
        setUserData((prev) => ({
          ...prev,
          [userId]: fetchedUser,
        }));
        console.log(`Successfully fetched and stored user data for ID: ${userId}`);
      } else {
        console.warn(`User data not found for ID: ${userId}`);
      }
    } catch (error) {
      console.error(`Failed to fetch user data for ID: ${userId}`, error);
    }
  }, [userData]);

  useEffect(() => {
    if (!user) return;

    (async () => {
      try {
        const data = await getConnections(user.token);

        const pending = data.pendingIncoming || [];
        setPendingIncomingIds(pending);

        const userPromises = pending.map((request: ConnectionProvider) => userService.getUser(request.userId));
        const users = await Promise.all(userPromises);

        const userMap = users.reduce(
          (acc, user) => {
            acc[user.id] = user;
            return acc;
          },
          {} as { [key: string]: CurrentUserResponseDTO }
        );

        setUserData(userMap);
      } catch (error) {
        console.error('Failed to fetch connections or user data:', error);
      }
    })();
  }, [user]);

  useEffect(() => {
    connectionUpdates.forEach((update) => {
      if (update.action === 'NEW_REQUEST') {
        setPendingIncomingIds((prev) => {
          if (prev.some(req => req.connectionId === update.connection.connectionId)) {
            return prev;
          }
          return [...prev, update.connection];
        });
        fetchUserData(update.connection.userId);
      } else if (['REQUEST_ACCEPTED', 'REQUEST_REJECTED'].includes(update.action)) {
        setPendingIncomingIds((prev) =>
            prev.filter((req) => req.connectionId !== update.connection.connectionId)
        );
      }
    });
  }, [connectionUpdates, fetchUserData]);

  const handleAccept = (connectionId: number) => {
    acceptConnectionRequest(connectionId);
  };

  const handleReject = (connectionId: number) => {
    rejectConnectionRequest(connectionId);
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
                  className="text-sm text-destructive hover:underline"
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
}
