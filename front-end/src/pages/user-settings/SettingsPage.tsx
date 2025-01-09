import UserPreferencesCard from './components/UserPreferencesCard';
import UserAttributesCard from './components/UserAttributesCard';
import UserProfileCard from './components/UserProfileCard';
import { useAuth } from '@/features/authentication';
import { useEffect, useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { SettingsContext } from './SettingsContext';
import { GenderContext } from '@/features/gender';
import { genderService } from '@/features/gender';
import UserAccountCard from './components/UserAccountCard';
import { toast } from "sonner"
import { meService } from '@/features/user';
import { SettingsResponseDTO, UserGenderType } from '@/api/types';

const SettingsPage = () => {
    const [settings, setSettings] = useState<SettingsResponseDTO | null>(null);
    const [genders, setGenders] = useState<UserGenderType[] | null>(null);
    const { user } = useAuth();

    if (!user) return null;

    useEffect(() => {
        const fetchSettings = async () => {
            try {
                const response = await meService.getUserParameters();
                setSettings(response);
            } catch (error) {
                console.error('Error fetching settings: ', error);
                throw error;
            }
        };

        const fetchGenders = async () => {
            try {
                const genders = await genderService.getGenders();
                setGenders(genders);
            } catch (error) {
                console.error('Failed fetching genders:', error);
            }
        };

        fetchGenders();
        fetchSettings();
    }, []);

    const refreshSettings = async () => {
        try {
            const response = await meService.getUserParameters();
            setSettings(response);
        } catch (error) {
            console.error('Error fetching settings: ', error);
            toast.error('Failed to refresh settings');
        }
    };

    return (
        <SettingsContext.Provider
            value={ {settings, refreshSettings}}
        >
            <GenderContext.Provider value={genders}>
                <div>
                    <Tabs defaultValue="account">
                        <TabsList className="w-full">
                            <TabsTrigger value="account" className="w-full">
                                Account
                            </TabsTrigger>
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
                        <TabsContent value="account">
                            <UserAccountCard />
                        </TabsContent>
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
