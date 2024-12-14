import { IoSettingsOutline } from 'react-icons/io5';
import { FaRegUserCircle } from 'react-icons/fa';
import {useEffect, useState} from "react";
import {userProfile} from '@/types/api.ts';
import {getUserParameters} from "@/pages/UserService.ts";

const UserInfo = () => {
  const [profile, setProfile] = useState<userProfile | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const data = await getUserParameters();
                console.log('✅ User profile fetched successfully:', data);
                setProfile(data);
            } catch (err) {
                console.error('❌ Error fetching user profile:', err);
                setError('Failed to load user profile.');
            } finally {
                setLoading(false);
            }
        };

        fetchUserProfile();
    }, []);

    if (loading) return <div>Loading user info...</div>;
    if (error) return <div>{error}</div>;

  return (
    <div className="relative flex w-full items-center bg-primary-200 text-text cursor-pointer">
      {/* <IoSettingsOutline className="absolute right-4 top-4 h-8 w-8 rounded-3xl bg-primary-300 p-1.5 text-primary-50 hover:cursor-pointer hover:bg-primary-400" /> */}
      <FaRegUserCircle className=" mr-2 p-2 h-20 w-20 text-text-600" />
      <div>
        <h2 className="ml-2 text-2xl font-bold text-text-700">{profile?.firstName || 'No name'} {profile?.lastName}</h2>
        <h2 className="ml-2 text-lg font-bold text-text-700">{profile?.alias || 'Alias'}</h2>
      </div>
    </div>
  );
};

export default UserInfo;
