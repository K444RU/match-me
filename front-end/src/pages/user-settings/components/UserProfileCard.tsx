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
const UserProfileCard = () => {
    const settings = useContext(SettingsContext);
    const [firstName, setFirstName] = useState<string | null>();
    const [lastName, setLastName] = useState<string | null>();
    const [alias, setAlias] = useState<string | null>();
/*     const [email, setEmail] = useState<string | null>();
    const [number, setNumber] = useState<string | null>(); */
    const [city, setCity] = useState<string | null>();

    useEffect(() => {
        if (settings) {
            setFirstName(settings.firstName ?? '');
            setLastName(settings.lastName ?? '');
            setAlias(settings.alias ?? '');
            setCity(settings.city ?? '');
        }
    }, [settings]);

    return (
        <Card className="w-[350px]">
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
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">City</Label>
                            <Input
                                id="name"
                                placeholder="City"
                                value={city ?? ''}
                                onChange={(e) => setCity(e.target.value)}
                            />
                        </div>
                    </div>
                </form>
            </CardContent>
            <CardFooter className="flex justify-end">
                <Button>Save</Button>
            </CardFooter>
        </Card>
    );
};

export default UserProfileCard;
