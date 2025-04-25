import { CurrentUserResponseDTO } from '@/api/types';
import { userService } from '@features/user';
import { useAuth } from '@features/authentication';
import { getConnections, useCommunication } from '@features/chat';
import { useCallback, useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Link } from 'react-router-dom';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import UserAvatar from '@/pages/chats/components/UserAvatar.tsx';

interface ConnectionProvider {
  connectionId: number;
  userId: number;
}

interface ConnectionRequestHandlerProps {
  onNavigate?: () => void;
}

export default function ConnectionRequestHandler({ onNavigate }: ConnectionRequestHandlerProps) {
  const { user } = useAuth();
  const { connectionUpdates, acceptConnectionRequest, rejectConnectionRequest } = useCommunication();
  const [pendingIncomingIds, setPendingIncomingIds] = useState<ConnectionProvider[]>([]);
  const [userData, setUserData] = useState<{ [key: string]: CurrentUserResponseDTO }>({});
  const [rejectingConnection, setRejectingConnection] = useState<{ connectionId: number; displayName: string } | null>(null);

  const fetchUserData = useCallback(
      async (userId: number) => {
        if (userData[userId]) {
          return;
        }
        try {
          console.log(`Fetching user data for ID: ${userId}`);
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
      },
      [userData]
  );

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
              if (user?.id) {
                acc[user.id] = user;
              }
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
          if (prev.some((req) => req.connectionId === update.connection.connectionId)) {
            return prev;
          }
          return [...prev, update.connection];
        });
        void fetchUserData(update.connection.userId);
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

  const handleLinkClick = () => {
    if (onNavigate) {
      onNavigate();
    }
  };

  return (
      <div className="space-y-4 max-w-[425px]">
        <h3 className="text-sm font-semibold text-card-foreground">Pending Incoming Requests</h3>
        {pendingIncomingIds.length === 0 ? (
            <div className="flex h-full items-center justify-center">
              <p className="text-sm text-muted-foreground">No pending requests.</p>
            </div>
        ) : (
            pendingIncomingIds.map((request) => {
              const requester = userData[request.userId];
              const isLoading = !requester;
              const firstName = requester?.firstName ?? '';
              const lastName = requester?.lastName ?? '';
              const alias = requester?.alias;
              const profilePicture = requester?.profilePicture;
              const nameString = `${firstName} ${lastName}`.trim();
              const displayName = isLoading
                  ? `Loading user ${request.userId}...`
                  : alias
                      ? `${nameString} (${alias})`
                      : nameString || `User ${request.userId}`;
              const avatarName = isLoading ? '?' : nameString || `User ${request.userId}`;

              return (
                  <div
                      key={request.connectionId}
                      className="flex justify-between rounded-md p-2 duration-100 hover:bg-accent group sm:flex-row flex-col sm:items-center gap-2"
                  >
                    <div className="flex items-center gap-2 overflow-hidden flex-grow min-w-0">
                      <Link
                          to={`/${request.userId}/profile`}
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
                          to={`/${request.userId}/profile`}
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
                    <div className="flex flex-nowrap gap-2 shrink-0 sm:ml-auto">
                      <Button
                          onClick={() => handleAccept(request.connectionId)}
                          variant="default"
                          size="sm"
                          aria-label={`Accept connection request from ${displayName}`}
                      >
                        Accept
                      </Button>
                      <Button
                          onClick={() =>
                              setRejectingConnection({ connectionId: request.connectionId, displayName })
                          }
                          variant="destructive"
                          size="sm"
                          aria-label={`Reject connection request from ${displayName}`}
                      >
                        Reject
                      </Button>
                    </div>
                  </div>
              );
            })
        )}
        <Dialog open={!!rejectingConnection} onOpenChange={(open) => !open && setRejectingConnection(null)}>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle>Confirm Rejection</DialogTitle>
              <DialogDescription>
                Are you sure you want to reject the connection request from {rejectingConnection?.displayName}?<br />
                This action will notify {rejectingConnection?.displayName} that the request was declined.
              </DialogDescription>
            </DialogHeader>
            <DialogFooter>
              <Button variant="outline" onClick={() => setRejectingConnection(null)}>
                Cancel
              </Button>
              <Button
                  variant="destructive"
                  onClick={() => {
                    if (rejectingConnection) {
                      handleReject(rejectingConnection.connectionId);
                      setRejectingConnection(null);
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