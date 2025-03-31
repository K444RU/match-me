import { CurrentUserResponseDTO } from '@/api/types';
import { useAuth } from '@features/authentication';
import { disconnectConnection, getConnections } from '@features/chat';
import { userService } from '@features/user';
import { useEffect, useState } from 'react';

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

export default function ActiveConnections() {
  const { user } = useAuth();
  const [activeConnections, setActiveConnections] = useState<ConnectionProvider[]>([]);
  const [userData, setUserData] = useState<{ [key: string]: CurrentUserResponseDTO }>({});

  useEffect(() => {
    if (!user) return;

    (async () => {
      try {
        const data = await getConnections(user.token);
        console.log('Connections data:', data);
        const active = data.active || [];
        setActiveConnections(active);

        const userPromises = active.map((connection: ConnectionProvider) => userService.getUser(connection.userId));
        const userResponses = await Promise.all(userPromises);
        const users = userResponses.map((response) => response.data);

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

  const handleDisconnect = async (connectionId: number) => {
    try {
      await disconnectConnection(connectionId.toString(), user!.token);
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
}
