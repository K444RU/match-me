import { useContext, useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { SettingsContext } from '../SettingsContext';
import { updateSettings } from '@/features/user/services/UserService';
import { toast } from 'sonner';
import MotionSpinner from '@/components/animations/MotionSpinner';

const UserProfileCard = () => {
    const settingsContext = useContext(SettingsContext);
    if (!settingsContext) return null;
    const { settings, refreshSettings } = settingsContext;
    const [firstName, setFirstName] = useState<string | null>();
    const [lastName, setLastName] = useState<string | null>();
    const [alias, setAlias] = useState<string | null>();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (settings) {
            setFirstName(settings.firstName ?? '');
            setLastName(settings.lastName ?? '');
            setAlias(settings.alias ?? '');
        }
    }, [settings]);

    const handleUpdate = async () => {
        if (!settings) return;
        
        setLoading(true);
        try {
            if (!firstName || !lastName || !alias) return;
            await updateSettings(
                {
                    ...settings,
                    firstName,
                    lastName,
                    alias,
                },
                'profile'
            );
            refreshSettings();
            toast.success('Profile updated successfully');
        } catch (error) {
            toast.error('Failed to update profile');
            console.error('Error updating profile:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card className="h-[475px] w-full border-none shadow-none">
            <CardHeader>
                <CardTitle>Profile</CardTitle>
                <CardDescription>
                    Edit your profile settings here.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">First Name</Label>
                            <Input
                                id="name"
                                placeholder="First Name"
                                value={firstName ?? ''}
                                onChange={(e) => setFirstName(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">Last Name</Label>
                            <Input
                                id="name"
                                placeholder="Last Name"
                                value={lastName ?? ''}
                                onChange={(e) => setLastName(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">Alias</Label>
                            <Input
                                id="name"
                                placeholder="Alias"
                                value={alias ?? ''}
                                onChange={(e) => setAlias(e.target.value)}
                            />
                        </div>
                    </div>
                </form>
            </CardContent>
            <CardFooter className="flex justify-end">
            <Button onClick={handleUpdate} disabled={loading}>
                    {loading ? (
                        <>
                            Updating <MotionSpinner />
                        </>
                    ) : (
                        'Update'
                    )}
                </Button>
            </CardFooter>
        </Card>
    );
};

export default UserProfileCard;
