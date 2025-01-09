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
import { GenderContext } from '@/features/gender';
import { CitySuggestions } from '@/pages/profile-completion/components/CitySuggestions';
import { useDebounce } from '@/lib/hooks/useDebounce';
import { City } from '@/pages/profile-completion/types/types';
import InputField from '@/components/ui/forms/InputField';
import { toast } from 'sonner';
import { userService } from '@/features/user';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Skeleton } from '@/components/ui/skeleton';

const UserAttributesCard = () => {
    const settingsContext = useContext(SettingsContext);
    if (!settingsContext) return null;
    const { settings, refreshSettings } = settingsContext;
    const genders = useContext(GenderContext);

    const [city, setCity] = useState<string>();
    const [longitude, setLongitude] = useState<number | null>(null);
    const [latitude, setLatitude] = useState<number | null>(null);
    const [gender, setGender] = useState<number | null>(null);
    const [birthDate, setBirthDate] = useState<Date | undefined>(undefined);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const debouncedCitySearchValue = useDebounce(city as string, 1000);
    const [loading, setLoading] = useState(false);

    const handleCitySelect = async (city: City) => {
        setShowSuggestions(false);
        setCity(city.name);
        setLongitude(city.longitude);
        setLatitude(city.latitude);
    };

    const handleCityInputChange = (value: string) => {
        setCity(value);
        setShowSuggestions(true);
    };

    const handleUpdate = async () => {
        if (!settings) return;

        setLoading(true);
        try {
            if (!city || !latitude || !longitude || !birthDate || !gender)
                return;
            await userService.updateAttributesSettings({
                city,
                longitude,
                latitude,
                birth_date: birthDate.toISOString().split('T')[0],
                gender_self: gender,
            });
            refreshSettings();
            toast.success('Attributes updated successfully');
        } catch (error) {
            toast.error('Failed to update attributes');
            console.error('Error updating attributes:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (settings) {
            setCity(settings.city ?? '');
            setLongitude(settings.longitude ?? null);
            setLatitude(settings.latitude ?? null);
            setGender(settings.genderSelf ?? null);
            setBirthDate(
                settings.birthDate ? new Date(settings.birthDate) : undefined
            );
        }
    }, [settings]);

    return (
        <Card className="h-[475px] w-full border-none shadow-none">
            <CardHeader>
                <CardTitle>Attributes</CardTitle>
                <CardDescription>Tell us about yourself</CardDescription>
            </CardHeader>
            <CardContent>
                <form>
                    <div className="grid w-full items-center gap-4">
                        <div className="relative flex flex-col space-y-1.5">
                            <Label htmlFor="name">Birth Date</Label>
                            {birthDate !== undefined ? (
                                <DatePicker
                                    selectedDate={birthDate}
                                    onDateChange={(dateString: string) =>
                                        setBirthDate(new Date(dateString))
                                    }
                                />
                            ) : (
                                <Skeleton className="w=full h-[40px] bg-primary" />
                            )}
                        </div>
                        <div className="relative flex flex-col space-y-1.5">
                            <Label htmlFor="city">City</Label>
                            {city !== undefined && city !== null ? (
                                <InputField
                                    type="text"
                                    name="city"
                                    placeholder="Enter your city"
                                    value={city}
                                    onChange={handleCityInputChange}
                                    onFocus={() => setShowSuggestions(true)}
                                    onBlur={() => {
                                        setTimeout(() => {
                                            setShowSuggestions(false);
                                        }, 500);
                                    }}
                                />
                            ) : (
                                <Skeleton className="h-[40px] w-full" />
                            )}
                            <div
                                className={`absolute top-full z-10 w-full ${!showSuggestions ? `hidden` : ``}`}
                            >
                                <CitySuggestions
                                    searchTerm={debouncedCitySearchValue}
                                    onCitySelect={handleCitySelect}
                                    visible={showSuggestions}
                                />
                            </div>
                        </div>
                        <div className="flex flex-col space-y-1.5">
                            <Label htmlFor="selfGender">Gender</Label>
                            {gender !== null && genders !== null ? (
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

export default UserAttributesCard;
