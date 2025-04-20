import UserPreferencesCard from './components/UserPreferencesCard';
import UserAttributesCard from './components/UserAttributesCard';
import UserProfileCard from './components/UserProfileCard';
import { useAuth } from '@/features/authentication';
import { useEffect, useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { SettingsContext } from './SettingsContext';
import UserAccountCard from './components/UserAccountCard';
import { toast } from "sonner"
import { meService } from '@/features/user';
import { SettingsResponseDTO } from '@/api/types';

const SettingsPage = () => {
    const [settings, setSettings] = useState<SettingsResponseDTO | null>(null);
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
                <div>
                    <Tabs defaultValue="account">
                        <TabsList className="w-full grid grid-cols-4">
                            <TabsTrigger value="account">
                                Account
                            </TabsTrigger>
                            <TabsTrigger value="profile">
                                Profile
                            </TabsTrigger>
                            <TabsTrigger value="preferences">
                                Preferences
                            </TabsTrigger>
                            <TabsTrigger value="attributes">
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
        </SettingsContext.Provider>
    );
};

export default SettingsPage;
