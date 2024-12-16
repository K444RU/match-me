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
import { toast } from 'sonner';
import { updateSettings } from '@/features/user/services/UserService';
import MotionSpinner from '@/components/animations/MotionSpinner';

const UserAccountCard = () => {
    const settingsContext = useContext(SettingsContext);
    if (!settingsContext) return null;
    const { settings, refreshSettings } = settingsContext;
    const [email, setEmail] = useState<string>('');
    const [countryCode, setCountryCode] = useState<string>('');
    const [number, setNumber] = useState<string>('');
    const [loading, setLoading] = useState(false);

    const handleUpdate = async () => {
        if (!settings) return;

        setLoading(true);
        try {
            if (!email || !number || !countryCode) return;
            await updateSettings(
                {
                    ...settings,
                    email,
                    number: `${countryCode} ${number}`,
                },
                'account'
            );
            refreshSettings();
            toast.success('Account updated successfully');
        } catch (error) {
            toast.error('Failed to update account');
            console.error('Error updating account:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (settings) {
            setEmail(settings.email ?? '');
            const [code, phoneNumber] = settings.number.split(' ');
            setCountryCode(code ?? '');
            setNumber(phoneNumber ?? '');
        }
    }, [settings]);

    return (
        <Card className="h-[475px] w-full border-none shadow-none">
            <CardHeader>
                <CardTitle>Account</CardTitle>
                <CardDescription>
                    Edit your account settings here.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        <div className="flex gap-4">
                            <div className="flex w-1/3 flex-col space-y-1.5">
                                <Label htmlFor="countryCode">
                                    Country Code
                                </Label>
                                <Input
                                    id="countryCode"
                                    placeholder="Country Code"
                                    value={countryCode}
                                    onChange={(e) =>
                                        setCountryCode(e.target.value)
                                    }
                                />
                            </div>
                            <div className="flex w-2/3 flex-col space-y-1.5">
                                <Label htmlFor="phoneNumber">
                                    Phone Number
                                </Label>
                                <Input
                                    id="phoneNumber"
                                    placeholder="Phone Number"
                                    value={number}
                                    onChange={(e) => setNumber(e.target.value)}
                                />
                            </div>
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

export default UserAccountCard;
