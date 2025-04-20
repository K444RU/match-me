import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Slider } from '@/components/ui/slider';
import { GenderContext } from '@/features/gender';
import { userService } from '@/features/user';
import { zodResolver } from '@hookform/resolvers/zod';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';
import { SettingsContext } from '../SettingsContext';

const preferencesSchema = z.object({
  genderOther: z.number({ required_error: 'Preferred gender is required.' }),
  distance: z.number().min(50).max(300),
  ageRange: z
    .array(z.number())
    .length(2, 'Age range must have two values.')
    .refine((data) => data[0] >= 18 && data[1] <= 120 && data[0] <= data[1], {
      message: 'Invalid age range (must be 18-120).',
    }),
  probabilityTolerance: z.number().min(0.1).max(1.0),
});

type PreferencesFormData = z.infer<typeof preferencesSchema>;

export default function UserPreferencesCard() {
  const settingsContext = useContext(SettingsContext);
  const genders = useContext(GenderContext);
  const [loading, setLoading] = useState(false);

  const form = useForm<PreferencesFormData>({
    resolver: zodResolver(preferencesSchema),
    defaultValues: {
      genderOther: undefined,
      distance: 50,
      ageRange: [18, 120],
      probabilityTolerance: 0.5,
    },
  });

  useEffect(() => {
    if (settingsContext?.settings) {
      form.reset({
        genderOther:
          typeof settingsContext.settings.genderOther === 'number' ? settingsContext.settings.genderOther : undefined,
        distance: settingsContext.settings.distance ?? 50,
        ageRange: [settingsContext.settings.ageMin ?? 18, settingsContext.settings.ageMax ?? 120],
        probabilityTolerance: settingsContext.settings.probabilityTolerance ?? 0.5,
      });
    } else {
      form.reset({
        genderOther: undefined,
        distance: 50,
        ageRange: [18, 120],
        probabilityTolerance: 0.5,
      });
    }
  }, [settingsContext?.settings, form.reset]);

  const watchedAgeRange = form.watch('ageRange');
  const watchedDistance = form.watch('distance');
  const watchedTolerance = form.watch('probabilityTolerance');

  if (!settingsContext || !settingsContext.settings) {
    return (
      <Card className="no-scrollbar flex h-[475px] w-full items-center justify-center">
        <MotionSpinner />
      </Card>
    );
  }

  const onSubmit = async (values: PreferencesFormData) => {
    if (!settingsContext?.settings) return;

    setLoading(true);
    try {
      await userService.updatePreferencesSettings({
        gender_other: values.genderOther,
        distance: values.distance,
        age_min: values.ageRange[0],
        age_max: values.ageRange[1],
        probability_tolerance: values.probabilityTolerance,
      });
      await settingsContext.refreshSettings();
      toast.success('Preferences updated successfully');
    } catch (error) {
      toast.error('Failed to update preferences');
      console.error('Error updating preferences:', error, form.formState.errors);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="h-[475px] w-full">
      <CardHeader>
        <CardTitle>Preferences</CardTitle>
        <CardDescription>These settings determine how we find matches for you.</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <CardContent>
            <div className="grid w-full items-center gap-4">
              <FormField
                control={form.control}
                name="ageRange"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      Age range | {watchedAgeRange[0]} - {watchedAgeRange[1]}
                    </FormLabel>
                    <FormControl>
                      <DualRangeSlider min={18} max={120} step={1} value={field.value} onValueChange={field.onChange} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="distance"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel htmlFor="distance">Distance | {watchedDistance} km</FormLabel>
                    <FormControl>
                      <Slider
                        id="distance"
                        min={50}
                        max={300}
                        step={10}
                        defaultValue={[50]}
                        value={[field.value]}
                        onValueChange={(value) => field.onChange(value[0])}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="probabilityTolerance"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel htmlFor="probabilityTolerance">
                      Probability Tolerance | {watchedTolerance.toFixed(1)}
                    </FormLabel>
                    <FormControl>
                      <Slider
                        id="probabilityTolerance"
                        min={0.1}
                        max={1.0}
                        step={0.1}
                        value={[field.value]}
                        onValueChange={(value) => field.onChange(value[0])}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="genderOther"
                render={({ field }) => (
                  <FormItem className="w-full">
                    <FormLabel htmlFor="genderOther">Gender</FormLabel>
                    <Select
                      key={`gender-select-${field.value}`}
                      value={field.value?.toString() ?? ''}
                      onValueChange={(value) => field.onChange(Number(value))}
                    >
                      <FormControl>
                        <SelectTrigger id="genderOther" className="w-full">
                          <SelectValue placeholder="Select a gender..." />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent position="popper">
                        {genders &&
                          genders.map((gender) => (
                            <SelectItem key={gender.id} value={gender.id.toString()}>
                              {gender.name}
                            </SelectItem>
                          ))}
                      </SelectContent>
                    </Select>
                    <FormDescription>What gender do you prefer?</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          </CardContent>
          <CardFooter className="flex justify-end">
            <Button type="submit" disabled={loading}>
              {loading ? (
                <>
                  Updating <MotionSpinner />
                </>
              ) : (
                'Update'
              )}
            </Button>
          </CardFooter>
        </form>
      </Form>
    </Card>
  );
}
