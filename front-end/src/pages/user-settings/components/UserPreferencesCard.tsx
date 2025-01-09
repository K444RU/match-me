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
import { GenderContext } from '@/features/gender';
import { toast } from 'sonner';
import { userService } from '@/features/user';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Skeleton } from '@/components/ui/skeleton';

const UserPreferencesCard = () => {
    const settingsContext = useContext(SettingsContext);
    const genders = useContext(GenderContext);
    const [gender, setGender] = useState<number | null>(null);
    const [distance, setDistance] = useState<number | null>(null);
    const [ageMin, setAgeMin] = useState<number | null>(null);
    const [ageMax, setAgeMax] = useState<number | null>(null);
    const [probabilityTolerance, setProbabilityTolerance] = useState<
        number | null
    >(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!settingsContext?.settings) return;

        setGender(settingsContext.settings.genderOther ?? null);
        setDistance(settingsContext.settings.distance ?? null);
        setAgeMin(settingsContext.settings.ageMin ?? null);
        setProbabilityTolerance(settingsContext.settings.probabilityTolerance ?? null);
        setAgeMax(settingsContext.settings.ageMax ?? null);

        }, [settingsContext?.settings]);

    const handleUpdate = async () => {
        if (!settingsContext?.settings) return;

        setLoading(true);
        try {
            if (
                !gender ||
                !distance ||
                !ageMin ||
                !ageMax ||
                !probabilityTolerance
            )
                return;
            await userService.updatePreferencesSettings({
                gender_other: gender,
                distance,
                age_min: ageMin,
                age_max: ageMax,
                probability_tolerance: probabilityTolerance,
            });
            settingsContext.refreshSettings();
            toast.success('Preferences updated successfully');
        } catch (error) {
            toast.error('Failed to update preferences');
            console.error('Error updating preferences:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card className="h-[475px] w-full border-none shadow-none">
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
                            <Label htmlFor="probabilityTolerance">
                                Probability Tolerance
                            </Label>
                            <OneHandleSlider
                                name="probabilityTolerance"
                                min={0.1}
                                max={1.0}
                                value={probabilityTolerance}
                                step={0.1}
                                onChange={setProbabilityTolerance}
                                showInputField={false}
                            />
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="otherGender">Gender</Label>
                            {gender !== null && genders !== null ? (
                                <>
                                    <Select
                                        value={gender?.toString()}
                                        onValueChange={(value) =>
                                            setGender(Number(value))
                                        }
                                    >
                                        <SelectTrigger id="otherGender">
                                            <SelectValue />
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
                                </>
                            ) : (
                                <Skeleton className="h-[40px] w-full rounded-md border border-[#e5e7eb]" />
                            )}
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

export default UserPreferencesCard;
