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
import { Label } from '@/components/ui/label';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { SettingsContext } from '../SettingsContext';
import DatePicker from '@/components/ui/forms/DatePicker';
import { GenderContext } from '@/features/gender/GenderContext';
const UserAttributesCard = () => {
    const settings = useContext(SettingsContext);
    const genders = useContext(GenderContext);

/*     const [longitude, setLongitude] = useState<number | null>(null);
    const [latitude, setLatitude] = useState<number | null>(null); */
    const [gender, setGender] = useState<number | null>(null);
    const [birthDate, setBirthDate] = useState<Date | undefined>(undefined);

    useEffect(() => {
        if (settings) {
/*             setLongitude(settings.longitude ?? null);
            setLatitude(settings.latitude ?? null); */
            setGender(settings.genderSelf ?? null);
            setBirthDate(
                settings.birthDate ? new Date(settings.birthDate) : undefined
            );
        }
    }, [settings]);

    useEffect(() => {
        console.log('birthDate changed:', birthDate);
    }, [birthDate]);

    return (
        <Card className="w-full h-[475px] border-none shadow-none">
            <CardHeader>
                <CardTitle>Attributes</CardTitle>
                <CardDescription>Tell us about yourself</CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="name">Birth Date</Label>
                            <DatePicker
                                selectedDate={birthDate}
                                onDateChange={(dateString: string) =>
                                    setBirthDate(new Date(dateString))
                                }
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="selfGender">Gender</Label>
                            <Select
                                value={gender?.toString()}
                                onValueChange={(value) =>
                                    setGender(Number(value))
                                }
                            >
                                <SelectTrigger id="selfGender">
                                    <SelectValue placeholder="Select a gender..." />
                                </SelectTrigger>
                                <SelectContent position="popper">
                                    {genders &&
                                        genders.map((gender) => (
                                            <SelectItem
                                                key={gender.id} // Add key prop
                                                value={gender.id.toString()}
                                            >
                                                {gender.name}
                                            </SelectItem>
                                        ))}
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                </form>
            </CardContent>
            <CardFooter className="flex justify-end">
                <Button>Update</Button>
            </CardFooter>
        </Card>
    );
};

export default UserAttributesCard;
