import { IoSettingsOutline } from 'react-icons/io5';
import { FaRegUserCircle } from 'react-icons/fa';
import { useEffect, useState } from 'react';
import { CurrentUser } from '@/types/api.ts';
import { getCurrentUser } from '@/pages/UserService.ts';
import { Skeleton } from '@/components/ui/Skeleton';

const UserInfo = () => {
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchCurrentUser = async () => {
            try {
                const data = await getCurrentUser();
                console.log('✅ User profile fetched successfully:', data);
                setCurrentUser(data);
            } catch (err) {
                console.error('❌ Error fetching user profile:', err);
                setError('Failed to load user profile.');
            } finally {
                setLoading(false);
            }
        };

        fetchCurrentUser();
    }, []);

    if (error) return <div>{error}</div>;

    return (
        <div className="relative mt-20 flex h-40 w-full items-center bg-primary-200 text-text">
            <IoSettingsOutline className="absolute right-4 top-4 h-8 w-8 rounded-3xl bg-primary-300 p-1.5 text-primary-50 hover:cursor-pointer hover:bg-primary-400" />
            <FaRegUserCircle className="ml-2 mr-2 h-20 w-20 text-text-600" />
            <div>
                <h2 className="ml-2 text-2xl font-bold text-text-700">
                    {currentUser?.firstName || (
                        <Skeleton className="h-[32px] w-[120px] bg-text-300 rounded-md" />
                    )}
                </h2>
                <h2 className="ml-2 text-lg font-bold text-text-700">
                    {currentUser?.alias || (
                        <Skeleton className="mt-1 h-[28px] w-[100px] bg-text-300 rounded-md" />
                    )}
                </h2>
            </div>
        </div>
    );
};

export default UserInfo;
