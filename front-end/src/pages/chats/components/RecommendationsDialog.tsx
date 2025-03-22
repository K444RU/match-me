import { MatchingRecommendationsDTO } from '@/api/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { connectionService } from '@/features/chat';
import { AxiosError } from 'axios';
import { UserPlus } from 'lucide-react';
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';

type ConnectionState = Record<string, 'idle' | 'loading' | 'sent'>;

async function fetchRecommendations() {
  try {
    const response = await connectionService.getRecommendations();
    console.log('Fetched recommendations:', response);
    return response;
  } catch (error) {
    toast.error('Failed to fetch recommendations');
    console.error('Error fetching recommendations:', error);
  }
}

function getInitials(firstName: string, lastName: string) {
  return `${firstName.charAt(0)}${lastName.charAt(0)}`;
}

const RecommendationsDialog = ({
  setIsOpen,
  isOpen,
}: {
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  isOpen: boolean;
}) => {
  const [recommendations, setRecommendations] = useState<MatchingRecommendationsDTO>();
  const [connectionStates, setConnectionStates] = useState<ConnectionState>({});

  useEffect(() => {
    if (isOpen) {
      const fetchData = async () => {
        const result = await fetchRecommendations();
        if (result) {
          setRecommendations(result);
          const initialStates =
            result?.recommendations?.reduce((acc, r) => {
              acc[String(r.userId)] = r.connectionStatus === 'PENDING_SENT' ? 'sent' : 'idle';
              return acc;
            }, {} as ConnectionState) || {};
          setConnectionStates(initialStates);
        }
      };
      fetchData();
    }
  }, [isOpen]);

  const handleSendConnectionRequest = useCallback(async (userId: number) => {
    try {
      setConnectionStates((prev) => ({ ...prev, [userId]: 'loading' }));
      setTimeout(() => {
        console.log(`Sent request to: ${userId}`);
        setConnectionStates((prev) => ({ ...prev, [userId]: 'sent' }));
      }, 1000);
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.data?.message === 'A pending request already exists from you to this user') {
          toast.info('Request already sent');
          setConnectionStates((prev) => ({ ...prev, [String(userId)]: 'sent' }));
        } else {
          toast.error('Failed to send connection request');
          console.error('Error sending connection request:', error);
          setConnectionStates((prev) => ({ ...prev, [String(userId)]: 'idle' }));
        }
      } else {
        toast.error('An unexpected error occurred');
        console.error('Unexpected error:', error);
        setConnectionStates((prev) => ({ ...prev, [String(userId)]: 'idle' }));
      }
    }
  }, []);

  const statusText = {
    PENDING_SENT: 'Request Sent',
    PENDING_RECEIVED: 'Request Received',
    ACCEPTED: 'Connected',
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Matching Recommendations</DialogTitle>
          <DialogDescription>View your latest matching recommendations here.</DialogDescription>
        </DialogHeader>
        <div className="space-y-4">
          {recommendations?.recommendations &&
            recommendations.recommendations.map((r) => (
              <div key={r.userId} className="flex justify-between rounded-md p-2 duration-100 hover:bg-text-100">
                <div className="flex items-center gap-2">
                  <Avatar>
                    <AvatarImage src={r.profilePicture} alt={`${r.firstName} avatar`} />
                    <AvatarFallback>{getInitials(r.firstName, r.lastName)}</AvatarFallback>
                  </Avatar>
                  <span>{`${r.firstName} ${r.lastName}`}</span>
                </div>
                {r.connectionStatus === 'NONE' ? (
                  <Button
                    onClick={() => handleSendConnectionRequest(r.userId)}
                    disabled={connectionStates[r.userId] === 'loading' || connectionStates[r.userId] === 'sent'}
                  >
                    {connectionStates[r.userId] === 'loading' ? (
                      <MotionSpinner />
                    ) : connectionStates[r.userId] === 'sent' ? (
                      'Sent'
                    ) : (
                      <>
                        Add <UserPlus />
                      </>
                    )}
                  </Button>
                ) : (
                  <span>{statusText[r.connectionStatus as keyof typeof statusText] || r.connectionStatus}</span>
                )}
              </div>
            ))}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default RecommendationsDialog;
