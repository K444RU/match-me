import UserPreferences from '@features/user/UserPreferences';
import UserAttributes from '@features/user/UserAttributes';
import { useAuth } from '@/features/authentication/AuthContext';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { getUserParameters } from '../UserService';
import { UserProfile } from '@/types/api';

const SettingsPage = () => {
    const [settings, setSettings] = useState<UserProfile | null>(null);
    const { user } = useAuth();

    if (!user) return;

    useEffect(() => {
        const fetchSettings = async () => {
            try {
                const response = await getUserParameters();
                setSettings(response);
              } catch (error) {
                console.error('Error fetching settings: ', error);
                throw error;
            }
        };

        fetchSettings();
    }, []);

    return (
        <div className="mx-auto h-screen max-w-[800px] items-center justify-center overflow-auto bg-background-200 px-5 pt-24">
            <UserPreferences />
            <UserAttributes />
        </div>
    );
};

export default SettingsPage;
