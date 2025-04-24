import { CurrentUserResponseDTO } from '@/api/types';
import { useAuth } from '@features/authentication';
import { getConnections, useCommunication } from '@features/chat';
import { userService } from '@features/user';
import { useEffect, useRef, useState, useCallback } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Link } from "react-router-dom";
import UserAvatar from "@/pages/chats/components/UserAvatar.tsx";

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

interface ActiveConnectionsProps {
  onNavigate?: () => void;
}

export default function ActiveConnections({ onNavigate }: ActiveConnectionsProps) {
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

    void fetchInitialData();

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

  const handleLinkClick = () => {
    if (onNavigate) {
      onNavigate();
    }
  };

  return (
      <div className="space-y-4 max-w-[425px]">
        <h3 className="text-sm font-semibold text-card-foreground">Active Connections</h3>
        {activeConnections.length === 0 ? (
            <div className="flex h-full items-center justify-center">
              <p className="text-sm text-muted-foreground">No active connections.</p>
            </div>
        ) : (
            activeConnections.map((connection) => {
              const connectedUser = userData[String(connection.userId)];
              const isLoading = !connectedUser;
              const firstName = connectedUser?.firstName ?? '';
              const lastName = connectedUser?.lastName ?? '';
              const alias = connectedUser?.alias;
              const profilePicture = connectedUser?.profilePicture;
              const nameString = `${firstName} ${lastName}`.trim();
              const displayName = isLoading
                  ? `Loading user ${connection.userId}...`
                  : alias
                      ? `${nameString} (${alias})`
                      : nameString || `User ${connection.userId}`;
              const avatarName = isLoading ? '?' : nameString || `User ${connection.userId}`;

              return (
                  <div
                      key={connection.connectionId}
                      className="flex justify-between rounded-md p-2 duration-100 hover:bg-accent group"
                  >
                    <div className="flex items-center gap-2 overflow-hidden mr-2 flex-grow">
                      <Link
                          to={`/${connection.userId}/profile`}
                          onClick={handleLinkClick}
                          aria-label={`View profile of ${displayName}`}
                          className={isLoading ? 'pointer-events-none' : ''}
                      >
                        <UserAvatar
                            name={avatarName}
                            profileSrc={profilePicture}
                            avatarClassName="size-8 flex-shrink-0"
                        />
                      </Link>
                      <Link
                          to={`/${connection.userId}/profile`}
                          onClick={handleLinkClick}
                          aria-label={`View profile of ${displayName}`}
                          className={`text-sm text-foreground truncate ${
                              isLoading ? 'pointer-events-none italic text-muted-foreground' : ''
                          }`}
                          title={displayName}
                      >
                        {displayName}
                      </Link>
                    </div>
                    <Button
                        onClick={() =>
                            setDisconnectingConnection({ connectionId: connection.connectionId, displayName })
                        }
                        variant="destructive"
                        aria-label={`Disconnect from ${displayName}`}
                    >
                      Disconnect
                    </Button>
                  </div>
              );
            })
        )}
        <Dialog open={!!disconnectingConnection} onOpenChange={(open) => !open && setDisconnectingConnection(null)}>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle>Confirm Disconnection</DialogTitle>
              <DialogDescription>
                Are you sure you want to disconnect from {disconnectingConnection?.displayName}?<br />
                This action will also remove the connection for {disconnectingConnection?.displayName}.
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