import { useContext, useEffect, useState } from 'react';
import OneHandleSlider from '../../../components/ui/forms/OneHandleSlider';
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
import MultiHandleSlider from '@/components/ui/forms/MultiRangeSlider';
import { SettingsContext } from '../SettingsContext';
import { GenderContext } from '@/features/gender/GenderContext';
const UserPreferencesCard = () => {
    const settings = useContext(SettingsContext);
    const genders = useContext(GenderContext);

    const [gender, setGender] = useState<number | null>(null);
    const [distance, setDistance] = useState<number | null>(null);
    const [ageMin, setAgeMin] = useState<number | null>(null);
    const [ageMax, setAgeMax] = useState<number | null>(null);

    useEffect(() => {
        if (settings) {
            setGender(settings.genderOther ?? null);
            setDistance(settings.distance ?? null);
            setAgeMin(settings.ageMin ?? null);
            setAgeMax(settings.ageMax ?? null);
        }
    }, [settings]);

    return (
        <Card className="w-full h-[475px] border-none shadow-none">
            <CardHeader>
                <CardTitle>Preferences</CardTitle>
                <CardDescription>
                    These settings determine how we find matches for you.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="ageRange">Age range</Label>
                            <MultiHandleSlider
                                min={18}
                                max={120}
                                minValue={ageMin}
                                maxValue={ageMax}
                                onInput={({ minValue, maxValue }) => {
                                    setAgeMin(minValue);
                                    setAgeMax(maxValue);
                                }}
                                showInputField={false}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="distance">Distance</Label>
                            <OneHandleSlider
                                name="distance"
                                min={50}
                                max={300}
                                value={distance}
                                step={10}
                                onChange={setDistance}
                                showInputField={false}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="otherGender">Gender</Label>
                            <Select
                                value={gender?.toString()}
                                onValueChange={(value) =>
                                    setGender(Number(value))
                                }
                            >
                                <SelectTrigger id="otherGender">
                                    <SelectValue placeholder="Select a gender..." />
                                </SelectTrigger>
                                <SelectContent position="popper">
                                    {genders &&
                                        genders.map((gender) => (
                                            <SelectItem
                                                key={gender.id}
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

export default UserPreferencesCard;
