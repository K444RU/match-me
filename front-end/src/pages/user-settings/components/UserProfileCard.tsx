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
import { Skeleton } from '@/components/ui/skeleton';
import { HOBBIES } from '@/assets/hobbies';
import MultipleSelector, { Option } from '@/components/ui/multi-select';

const UserProfileCard = () => {
    const settingsContext = useContext(SettingsContext);
    if (!settingsContext) return null;
    const { settings, refreshSettings } = settingsContext;
    const [firstName, setFirstName] = useState<string | null>();
    const [lastName, setLastName] = useState<string | null>();
    const [selectedHobbies, setSelectedHobbies] = useState<Option[] | undefined>([]);
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
        <Card className="h-[475px] w-full border-none shadow-none flex flex-col">
            <CardHeader>
                <CardTitle>Profile</CardTitle>
                <CardDescription>
                    Edit your profile settings here.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex gap-2">
                            <div className="flex flex-col space-y-1.5">
                                <Label htmlFor="firstName">First Name</Label>
                                {firstName !== undefined && firstName !== null ? (
                                    <Input
                                        id="firstName"
                                        placeholder="First Name"
                                        value={firstName}
                                        onChange={(e) =>
                                            setFirstName(e.target.value)
                                        }
                                    />
                                ) : (
                                    <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
                                )}
                            </div>
                            <div className="flex flex-col space-y-1.5">
                                <Label htmlFor="lastName">Last Name</Label>
                                {lastName !== undefined && lastName !== null ? (
                                    <Input
                                        id="lastName"
                                        placeholder="Last Name"
                                        value={lastName}
                                        onChange={(e) =>
                                            setLastName(e.target.value)
                                        }
                                    />
                                ) : (
                                    <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
                                )}
                            </div>
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="alias">Alias</Label>
                            {alias !== undefined && alias !== null ? (
                                <Input
                                    id="alias"
                                    placeholder="Alias"
                                    value={alias}
                                    onChange={(e) => setAlias(e.target.value)}
                                />
                            ) : (
                                <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
                            )}
                        </div>
                        <div className="flex flex-col space-y-1.5">
                                <Label htmlFor="hobbies">Hobbies</Label>
                                <MultipleSelector
                                value={selectedHobbies}
                                onChange={setSelectedHobbies}
                                placeholder='Select your hobbies...'
                                defaultOptions={HOBBIES}
                                groupBy='category'
                                hideClearAllButton={true}
                                maxSelected={5}
                                hidePlaceholderWhenSelected={true}
                                />
                        </div>
                    </div>
                </form>
            </CardContent>
            <CardFooter className="flex justify-end mt-auto">
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
