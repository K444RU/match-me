import {CurrentUserResponseDTO} from '@/api/types';
import {useAuth} from '@features/authentication';
import {getConnections, useCommunication} from '@features/chat';
import {userService} from '@features/user';
import {useEffect, useRef, useState, useCallback} from 'react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

export default function ActiveConnections() {
  const { user } = useAuth();
  const { connectionUpdates, disconnectConnection } = useCommunication();
  const [activeConnections, setActiveConnections] = useState<ConnectionProvider[]>([]);
  const [userData, setUserData] = useState<{ [key: string]: CurrentUserResponseDTO }>({});
  const processedUpdatesCountRef = useRef(0);
  const [disconnectingConnection, setDisconnectingConnection] = useState<{ connectionId: number; displayName: string } | null>(null);

  useEffect(() => {
    if (!user?.token) {
      setActiveConnections([]);
      setUserData({});
      return;
    }

    let isMounted = true;

    const fetchInitialData = async () => {
      try {
        const data = await getConnections(user.token);

        const active = data.active || [];

        if (!isMounted) return;

        setActiveConnections(active);

        if (active.length > 0) {
          const userIdsToFetch = active.map((connection: ConnectionProvider) => connection.userId);
          const userPromises = userIdsToFetch.map((id: number) => userService.getUser(id));
          const users = await Promise.all(userPromises);

          if (!isMounted) return;

          const userMap = users.reduce(
              (acc, fetchedUser) => {
                if (fetchedUser?.id) {
                  acc[String(fetchedUser.id)] = fetchedUser;
                }
                return acc;
              },
              {} as { [key: string]: CurrentUserResponseDTO }
          );
          setUserData(userMap);
        } else {
          if (isMounted) setUserData({});
        }
      } catch (error) {
        console.error('[ActiveConnections] Failed to fetch initial connections or user data:', error);
        if (isMounted) {
          setActiveConnections([]);
          setUserData({});
        }
      }
    };

    fetchInitialData();

    return () => {
      isMounted = false;
    };
  }, [user]);

  useEffect(() => {
    const currentLength = connectionUpdates.length;
    const lastProcessedIndex = Math.min(processedUpdatesCountRef.current, currentLength);
    const newUpdates = connectionUpdates.slice(lastProcessedIndex);

    if (newUpdates.length > 0) {
      newUpdates.forEach((update) => {
        if (!update || !update.connection) return;

        const { action, connection } = update;
        const { connectionId, userId } = connection;

        if (action === 'REQUEST_ACCEPTED') {
          setActiveConnections((prev) => {
            if (!prev.some((conn) => conn.connectionId === connectionId)) {
              userService.getUser(userId)
                  .then(newUser => {
                    if (newUser?.id) {
                      setUserData(prevData => ({ ...prevData, [String(newUser.id)]: newUser }));
                    }
                  })
                  .catch(err => console.error(`[ActiveConnections] Failed to fetch user data for accepted connection ${connectionId}`, err));
              return [...prev, connection];
            }
            return prev;
          });
        } else if (action === 'DISCONNECTED') {
          setActiveConnections((prev) => {
            const connectionExists = prev.some(conn => conn.connectionId === connectionId);
            if (connectionExists) {
              return prev.filter((conn) => conn.connectionId !== connectionId);
            }
            return prev;
          });
        }
      });
      processedUpdatesCountRef.current = currentLength;
    }
  }, [connectionUpdates]);

  const handleDisconnect = useCallback((connectionId: number) => {
    disconnectConnection(connectionId);
  }, [disconnectConnection]);

  return (
      <div className="space-y-2 p-2 border rounded-md shadow-xs bg-card">
        <h3 className="text-sm font-semibold text-card-foreground">Active Connections</h3>
        {activeConnections.length === 0 ? (
            <p className="text-sm text-muted-foreground px-1 py-2">No active connections.</p>
        ) : (
            <ul className="space-y-1">
              {activeConnections.map((connection) => {
                const connectedUser = userData[String(connection.userId)];
                const displayName = connectedUser?.alias
                    ? `${connectedUser.firstName || ''} ${connectedUser.lastName || ''} (${connectedUser.alias})`.trim()
                    : connectedUser?.firstName || connectedUser?.lastName
                        ? `${connectedUser.firstName || ''} ${connectedUser.lastName || ''}`.trim()
                        : `User ${connection.userId}`;

                return (
                    <li key={connection.connectionId} className="flex items-center justify-between p-1 hover:bg-muted/50 rounded-md">
                <span className="text-sm text-foreground truncate pr-2" title={displayName}>
                  {displayName}
                </span>
                      <button
                          onClick={() => setDisconnectingConnection({ connectionId: connection.connectionId, displayName })}
                          className="ml-2 shrink-0 text-xs text-red-600 hover:text-red-800 hover:underline focus:outline-hidden focus:ring-1 focus:ring-red-500 rounded-sm px-1 py-0.5"
                          aria-label={`Disconnect from ${displayName}`}
                      >
                        Disconnect
                      </button>
                    </li>
                );
              })}
            </ul>
        )}
        <Dialog open={!!disconnectingConnection} onOpenChange={(open) => !open && setDisconnectingConnection(null)}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Confirm Disconnection</DialogTitle>
              <DialogDescription>
                Are you sure you want to disconnect from {disconnectingConnection?.displayName}? This action will also remove the connection for {disconnectingConnection?.displayName}.
              </DialogDescription>
            </DialogHeader>
            <DialogFooter>
              <Button variant="outline" onClick={() => setDisconnectingConnection(null)}>
                Cancel
              </Button>
              <Button
                  variant="destructive"
                  onClick={() => {
                    if (disconnectingConnection) {
                      handleDisconnect(disconnectingConnection.connectionId);
                      setDisconnectingConnection(null);
                    }
                  }}
              >
                Confirm
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
  );
}