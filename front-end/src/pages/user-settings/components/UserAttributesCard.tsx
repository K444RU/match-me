import { UserGenderEnum } from '@/api/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import GenderSelect from '@/components/forms/GenderSelect';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import DatePicker from '@/components/ui/forms/DatePicker';
import { Input } from '@/components/ui/input';
import { userService } from '@/features/user';
import { useDebounce } from '@/lib/hooks/use-debounce';
import CitySuggestions from '@/pages/profile-completion/components/CitySuggestions';
import { City } from '@/pages/profile-completion/types/types';
import { zodResolver } from '@hookform/resolvers/zod';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';
import { SettingsContext } from '../SettingsContext';

const attributesSchema = z.object({
  birthDate: z.date({ required_error: 'Birth date is required.' }),
  city: z.string().min(1, { message: 'City is required.' }),
  latitude: z.number({ required_error: 'Latitude is required.' }),
  longitude: z.number({ required_error: 'Longitude is required.' }),
  genderSelf: z.nativeEnum(UserGenderEnum, { required_error: 'Gender is required.' }),
});

type AttributesFormData = z.infer<typeof attributesSchema>;

export default function UserAttributesCard() {
  const settingsContext = useContext(SettingsContext);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [loading, setLoading] = useState(false);

  const form = useForm<AttributesFormData>({
    resolver: zodResolver(attributesSchema),
    defaultValues: {
      birthDate: undefined,
      city: '',
      latitude: undefined,
      longitude: undefined,
      genderSelf: undefined,
    },
  });

  const cityValue = form.watch('city');
  const debouncedCitySearchValue = useDebounce(cityValue, 1000);

  useEffect(() => {
    if (settingsContext?.settings) {
      form.reset({
        city: settingsContext.settings.city ?? '',
        latitude: typeof settingsContext.settings.latitude === 'number' ? settingsContext.settings.latitude : undefined,
        longitude:
          typeof settingsContext.settings.longitude === 'number' ? settingsContext.settings.longitude : undefined,
        genderSelf:
          typeof settingsContext.settings.genderSelf === 'string' ? settingsContext.settings.genderSelf : undefined,
        birthDate: settingsContext.settings.birthDate ? new Date(settingsContext.settings.birthDate) : undefined,
      });
    } else {
      // Reset to default values if settings are not available
      form.reset({
        birthDate: undefined,
        city: '',
        latitude: undefined,
        longitude: undefined,
        genderSelf: undefined,
      });
    }
  }, [settingsContext?.settings, form.reset]);

  const handleCitySelect = async (city: City) => {
    setShowSuggestions(false);
    form.setValue('city', city.name, { shouldValidate: true });
    form.setValue('longitude', city.longitude, { shouldValidate: true });
    form.setValue('latitude', city.latitude, { shouldValidate: true });
    form.trigger(['city', 'latitude', 'longitude']);
  };

  const onSubmit = async (values: AttributesFormData) => {
    if (!settingsContext?.settings) return;

    setLoading(true);
    try {
      await userService.updateAttributesSettings({
        city: values.city,
        longitude: values.longitude,
        latitude: values.latitude,
        birth_date: values.birthDate.toISOString().split('T')[0],
        gender_self: values.genderSelf,
      });
      await settingsContext.refreshSettings();
      toast.success('Attributes updated successfully');
    } catch (error) {
      toast.error('Failed to update attributes');
      console.error('Error updating attributes:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!settingsContext || !settingsContext.settings) {
    return (
      <Card className="no-scrollbar flex h-[475px] w-full items-center justify-center">
        <MotionSpinner />
      </Card>
    );
  }

  return (
    <Card className="min-h-[475px] w-full">
      <CardHeader>
        <CardTitle>Attributes</CardTitle>
        <CardDescription>Tell us about yourself</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <CardContent>
            <div className="grid w-full items-center gap-4">
              <FormField
                control={form.control}
                name="birthDate"
                render={({ field }) => (
                  <FormItem className="flex flex-col">
                    <FormLabel>Birth Date</FormLabel>
                    <FormControl>
                      <DatePicker selectedDate={field.value} onDateChange={field.onChange} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="city"
                render={({ field }) => (
                  <FormItem className="relative">
                    <FormLabel htmlFor="city">City</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        type="text"
                        placeholder="Enter your city"
                        className=""
                        onFocus={() => setShowSuggestions(true)}
                        onBlur={() => {
                          setTimeout(() => {
                            setShowSuggestions(false);
                          }, 500);
                        }}
                      />
                    </FormControl>
                    <FormDescription>This will be used to find matches near you.</FormDescription>
                    <FormMessage />
                    <div className={`absolute top-[calc(100%-1.5rem)] z-10 w-full ${!showSuggestions ? `hidden` : ``}`}>
                      <CitySuggestions
                        searchTerm={debouncedCitySearchValue}
                        onCitySelect={handleCitySelect}
                        visible={showSuggestions}
                      />
                    </div>
                  </FormItem>
                )}
              />

              <GenderSelect control={form.control} name="genderSelf" description="What gender are you?" />
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
