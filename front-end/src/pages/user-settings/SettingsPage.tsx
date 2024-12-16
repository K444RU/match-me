import UserPreferencesCard from './components/UserPreferencesCard';
import UserAttributesCard from './components/UserAttributesCard';
import UserProfileCard from './components/UserProfileCard';
import { useAuth } from '@/features/authentication/AuthContext';
import { useEffect, useState } from 'react';
import { getUserParameters } from '../../features/user/services/UserService';
import { Gender, UserProfile } from '@/types/api';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { SettingsContext } from './SettingsContext';
import { GenderContext } from '@/features/gender/GenderContext';
import { getGenders } from '@/features/gender/services/GenderService';

const SettingsPage = () => {
    const [settings, setSettings] = useState<UserProfile | null>(null);
    const [genders, setGenders] = useState<Gender[] | null>(null);
    const { user } = useAuth();

    if (!user) return null;

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

        const fetchGenders = async () => {
            try {
                const genders = await getGenders();
                setGenders(genders);
            } catch (error) {
                console.error('Failed fetching genders:', error);
            }
        };

        fetchGenders();
        fetchSettings();
    }, []);

    return (
        <SettingsContext.Provider value={settings}>
            <GenderContext.Provider value={genders}>
                <div>
                    <Tabs defaultValue="profile">
                        <TabsList className="w-full">
                            <TabsTrigger value="profile" className="w-full">
                                Profile
                            </TabsTrigger>
                            <TabsTrigger className="w-full" value="preferences">
                                Preferences
                            </TabsTrigger>
                            <TabsTrigger className="w-full" value="attributes">
                                Attributes
                            </TabsTrigger>
                        </TabsList>
                        <TabsContent value="profile">
                            <UserProfileCard />
                        </TabsContent>
                        <TabsContent value="preferences">
                            <UserPreferencesCard />
                        </TabsContent>
                        <TabsContent value="attributes">
                            <UserAttributesCard />
                        </TabsContent>
                    </Tabs>
                </div>
            </GenderContext.Provider>
        </SettingsContext.Provider>
    );
};

export default SettingsPage;
