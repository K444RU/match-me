import {CurrentUserResponseDTO} from '@/api/types';
import {useAuth} from '@features/authentication';
import {getConnections, useCommunication} from '@features/chat';
import {userService} from '@features/user';
import {useEffect, useState} from 'react';

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

export default function ActiveConnections() {
  const { user } = useAuth();
  const { connectionUpdates, disconnectConnection } = useCommunication();
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
      if (update.action === 'REQUEST_ACCEPTED') {
        setActiveConnections((prev) => [...prev, update.connection]);
      } else if (update.action === 'DISCONNECTED') {
        setActiveConnections((prev) =>
            prev.filter((conn) => conn.connectionId !== update.connection.connectionId)
        );
      }
    });
  }, [connectionUpdates]);

  const handleDisconnect = (connectionId: number) => {
    disconnectConnection(connectionId);
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
