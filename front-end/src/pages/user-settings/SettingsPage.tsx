import UserPreferencesCard from './components/UserPreferencesCard';
import UserAttributesCard from './components/UserAttributesCard';
import UserProfileCard from './components/UserProfileCard';
import { useAuth } from '@/features/authentication';
import { useEffect, useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { SettingsContext } from './SettingsContext';
import { GenderContext, genderService } from '@/features/gender';
import UserAccountCard from './components/UserAccountCard';
import { toast } from "sonner"
import { meService } from '@/features/user';
import { GenderTypeDTO, SettingsResponseDTO } from '@/api/types';

const SettingsPage = () => {
    const [settings, setSettings] = useState<SettingsResponseDTO | null>(null);
    const [genders, setGenders] = useState<GenderTypeDTO[] | null>(null);
    const { user } = useAuth();
    
    useEffect(() => {
        if (!user) return;

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
    }, [user]);

    if (!user) return null;

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
                            <TabsTrigger value="account">
                                <span className="text-background">Account</span>
                            </TabsTrigger>
                            <TabsTrigger value="profile">
                                <span className="text-background">Profile</span>
                            </TabsTrigger>
                            <TabsTrigger value="preferences">
                                <span className="text-background">Preferences</span>
                            </TabsTrigger>
                            <TabsTrigger value="attributes">
                                <span className="text-background">Attributes</span>
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
