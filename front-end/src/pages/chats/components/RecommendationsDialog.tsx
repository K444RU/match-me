import { useDispatch, useSelector } from 'react-redux';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { connectionService, useCommunication } from '@/features/chat';
import { UserPlus, X } from 'lucide-react';
import {Dispatch, SetStateAction, useCallback, useEffect, useState} from 'react';
import { toast } from 'sonner';
import UserAvatar from './UserAvatar';
import { Link } from 'react-router-dom';
import { getUserController } from '@/api/user-controller';
import { MatchingRecommendationsDTO, RecommendedUserDTO } from '@/api/types';
import { RootState, AppDispatch } from '@features/recommendations/store.ts';
import { setUsers } from '@/features/recommendations/userCacheSlice';

type ConnectionState = Record<string, 'idle' | 'loading' | 'sent'>;

const fetchUsersBatch = async (ids: number[]): Promise<RecommendedUserDTO[]> => {
    try {
        const response = await getUserController().getUsersBatch(ids);
        return response.users || [];
    } catch (error) {
        console.error('Error fetching users batch:', error);
        throw error;
    }
};

export default function RecommendationsDialog({
                                                  setIsOpen,
                                                  isOpen,
                                              }: {
    setIsOpen: Dispatch<SetStateAction<boolean>>;
    isOpen: boolean;
}) {
    const { sendConnectionRequest } = useCommunication();
    const [recommendedIds, setRecommendedIds] = useState<number[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [connectionStates, setConnectionStates] = useState<ConnectionState>({});
    const [pendingDismissalRecommendation, setPendingDismissalRecommendation] = useState<RecommendedUserDTO | null>(null);
    const cachedUsers = useSelector((state: RootState) => state.userCache.users);
    const dispatch = useDispatch<AppDispatch>();

    useEffect(() => {
        if (isOpen) {
            setIsLoading(true);
            const fetchData = async () => {
                try {
                    const recommendations: MatchingRecommendationsDTO = await connectionService.getRecommendations();
                    const ids = recommendations.recommendations || [];
                    setRecommendedIds(ids);
                    const missingIds = ids.filter(id => !cachedUsers[id]);
                    if (missingIds.length > 0) {
                        const users = await fetchUsersBatch(missingIds);
                        dispatch(setUsers(users));
                    }
                } catch (error) {
                    toast.error('Failed to fetch recommendations or user data');
                } finally {
                    setIsLoading(false);
                }
            };
            fetchData();
        }
    }, [isOpen, cachedUsers, dispatch]);

    const recommendedUsers = recommendedIds.map(id => cachedUsers[id]).filter(user => user !== undefined);

    const handleDismiss = async (userId: number) => {
        try {
            await connectionService.dismissRecommendations(userId);
            setRecommendedIds(prev => prev.filter(id => id !== userId));
            toast.success('Recommendation dismissed');
        } catch (error) {
            toast.error('Failed to dismiss recommendation');
            console.error('Error dismissing recommendation', error);
        }
    };

    const handleSendConnectionRequest = useCallback(async (userId: number) => {
        try {
            setConnectionStates((prev) => ({ ...prev, [userId]: 'loading' }));
            await sendConnectionRequest(userId);
            setConnectionStates((prev) => ({ ...prev, [userId]: 'sent' }));
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
                        {isLoading ? (
                            <div>Loading...</div>
                        ) : recommendedUsers.length > 0 ? (
                            recommendedUsers.map((r) => (
                                <div key={r.userId} className="flex justify-between rounded-md p-2 duration-100 hover:bg-accent group">
                                    <div className="flex items-center gap-2 overflow-hidden mr-2 flex-grow">
                                        <Link to={`/${r.userId}/profile`} onClick={() => setIsOpen(false)}>
                                            <UserAvatar name={`${r.firstName} ${r.lastName}`} profileSrc={r.profilePicture} />
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
                                        Dismiss <X className="h-4 w-4 text-muted-foreground hover:text-foreground" />
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