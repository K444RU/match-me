import { MatchingRecommendationsDTO, RecommendedUserDTO } from '@/api/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import {Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle} from '@/components/ui/dialog';
import { connectionService, useCommunication } from '@/features/chat';
import { UserPlus, X } from 'lucide-react';
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';
import UserAvatar from './UserAvatar';
import {Link} from "react-router-dom";

type ConnectionState = Record<string, 'idle' | 'loading' | 'sent'>;

async function fetchRecommendations() {
    try {
        return await connectionService.getRecommendations();
    } catch (error) {
        toast.error('Failed to fetch recommendations');
        console.error('Error fetching recommendations:', error);
    }
}

export default function RecommendationsDialog({
  setIsOpen,
  isOpen,
}: {
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  isOpen: boolean;
}) {
  const { sendConnectionRequest } = useCommunication();
  const [recommendations, setRecommendations] = useState<MatchingRecommendationsDTO>();
  const [connectionStates, setConnectionStates] = useState<ConnectionState>({});
  const [pendingDismissalRecommendation, setPendingDismissalRecommendation] = useState<RecommendedUserDTO | null>(null);

  useEffect(() => {
    if (isOpen) {
      const fetchData = async () => {
        const result = await fetchRecommendations();
        if (result) {
          setRecommendations(result);
        }
      };
      fetchData();
    }
  }, [isOpen]);

    const handleDismiss = async (userId: number) => {
        try {
            await connectionService.dismissRecommendations(userId);
            setRecommendations(prev => {
                if (!prev) return prev;
                return {
                    ...prev,
                    recommendations: prev.recommendations?.filter(r => r.userId !== userId),
                };
            });
            toast.success('Recommendation dismissed');
        } catch (error) {
            toast.error('Failed to dismiss recommendation');
            console.error('Error dismissing recommendation', error);
        }
    };

  const handleSendConnectionRequest = useCallback(async (userId: number) => {
    try {
      setConnectionStates((prev) => ({ ...prev, [userId]: 'loading' }));
      sendConnectionRequest(userId);
      setTimeout(() => {
        setConnectionStates((prev) => ({ ...prev, [userId]: 'sent' }));
      }, 1000);
    } catch (error) {
      toast.error('Failed to send a connection request');
      console.error('Error sending a connection request:', error);
      setConnectionStates((prev) => ({ ...prev, [userId]: 'idle' }));
    }
  }, [sendConnectionRequest]);

    return (
        <>
            <Dialog open={isOpen} onOpenChange={setIsOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Matching Recommendations</DialogTitle>
                        <DialogDescription>View your latest matching recommendations here.</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                        {recommendations?.recommendations && recommendations.recommendations.length > 0 ? (
                            recommendations.recommendations.map((r) => (
                                <div key={r.userId} className="flex justify-between rounded-md p-2 duration-100 hover:bg-accent group">
                                    <div className="flex items-center gap-2 overflow-hidden mr-2 flex-grow">
                                        <Link to={`/${r.userId}/profile`} onClick={() => setIsOpen(false)}>
                                        <UserAvatar name={`${r.firstName} ${r.lastName}`} />
                                        </Link>
                                        <Link to={`/${r.userId}/profile`} onClick={() => setIsOpen(false)}>
                                            <span>{`${r.firstName} ${r.lastName}`}</span>
                                        </Link>
                                    </div>
                                    <Button
                                        onClick={() => handleSendConnectionRequest(r.userId)}
                                        disabled={connectionStates[r.userId] === 'loading' || connectionStates[r.userId] === 'sent'}
                                    >
                                        {connectionStates[r.userId] === 'sent' ? (
                                            'Sent'
                                        ) : connectionStates[r.userId] === 'loading' ? (
                                            <>
                                                <MotionSpinner />{' '}
                                            </>
                                        ) : (
                                            <>
                                                Add <UserPlus />
                                            </>
                                        )}
                                    </Button>
                                    <Button
                                        onClick={() => setPendingDismissalRecommendation(r)}
                                        variant="destructive"
                                        className="ml-2"
                                    >
                                        Dismiss <X className="h-4 w-4 text-muted-foreground hover:text-foreground"/>
                                    </Button>
                                </div>
                            ))
                        ) : (
                            <div className="flex h-full items-center justify-center">
                                <p>No recommendations found 🥲</p>
                            </div>
                        )}
                    </div>
                </DialogContent>
            </Dialog>
            <Dialog open={!!pendingDismissalRecommendation} onOpenChange={(open) => !open && setPendingDismissalRecommendation(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirm Dismissal</DialogTitle>
                        <DialogDescription>
                            Are you sure you want to dismiss the recommendation for {pendingDismissalRecommendation ? `${pendingDismissalRecommendation.firstName} ${pendingDismissalRecommendation.lastName}` : ''}?
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setPendingDismissalRecommendation(null)}>
                            Cancel
                        </Button>
                        <Button
                            variant="destructive"
                            onClick={() => {
                                if (pendingDismissalRecommendation) {
                                    handleDismiss(pendingDismissalRecommendation.userId);
                                    setPendingDismissalRecommendation(null);
                                }
                            }}
                        >
                            Confirm
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    );
}