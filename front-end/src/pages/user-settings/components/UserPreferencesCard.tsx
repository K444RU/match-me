import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { GenderContext } from '@/features/gender';
import { userService } from '@/features/user';
import { useContext, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { SettingsContext } from '../SettingsContext';
import { Slider } from '@/components/ui/slider';

export default function UserPreferencesCard() {
  const settingsContext = useContext(SettingsContext);
  const genders = useContext(GenderContext);
  const [gender, setGender] = useState<number | null>(null);
  const [distance, setDistance] = useState<number | 50>(50);
  const [ageValues, setAgeValues] = useState<number[]>([]);
  const [probabilityTolerance, setProbabilityTolerance] = useState<number | 0.5>(0.5);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!settingsContext?.settings) return;

    setGender(settingsContext.settings.genderOther ?? null);
    setDistance(settingsContext.settings.distance ?? 50);
    setAgeValues([settingsContext.settings.ageMin ?? 18, settingsContext.settings.ageMax ?? 120]);
    setProbabilityTolerance(settingsContext.settings.probabilityTolerance ?? 0.5);
  }, [settingsContext?.settings]);

  const handleUpdate = async () => {
    if (!settingsContext?.settings) return;

    setLoading(true);
    try {
      if (!gender || !distance || !ageValues[0] || !ageValues[1] || !probabilityTolerance) return;
      await userService.updatePreferencesSettings({
        gender_other: gender,
        distance,
        age_min: ageValues[0],
        age_max: ageValues[1],
        probability_tolerance: probabilityTolerance,
      });
      await settingsContext.refreshSettings();
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
        <CardDescription>These settings determine how we find matches for you.</CardDescription>
      </CardHeader>
      <CardContent>
        <form>
          <div className="grid w-full items-center gap-4">
            <div className="space-y-2">
              <Label>Age range | {ageValues[0]} - {ageValues[1]}</Label>
              <DualRangeSlider
                min={18}
                max={120}
                step={1}
                value={ageValues}
                onValueChange={setAgeValues}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="distance">Distance | {distance} km</Label>
              <Slider
                min={50}
                max={300}
                step={10}
                value={[distance]}
                onValueChange={(value) => setDistance(value[0])}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="probabilityTolerance">Probability Tolerance | {probabilityTolerance}</Label>
              <Slider
                min={0.1}
                max={1.0}
                step={0.1}
                value={[probabilityTolerance]}
                onValueChange={(value) => setProbabilityTolerance(value[0])}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="otherGender">Gender</Label>
              {gender !== null && genders !== null ? (
                <>
                  <Select value={gender?.toString()} onValueChange={(value) => setGender(Number(value))}>
                    <SelectTrigger id="otherGender">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent position="popper">
                      {genders &&
                        genders.map((gender) => (
                          <SelectItem key={gender.id} value={gender.id.toString()}>
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
