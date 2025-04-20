import { HOBBIES } from '@/assets/hobbies';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import ProfilePictureUploader from '@/components/ui/forms/ProfilePictureUploader';
import { Input } from '@/components/ui/input';
import MultipleSelector from '@/components/ui/multi-select';
import { useAuth } from '@/features/authentication';
import { userService } from '@/features/user';
import { hobbiesById } from '@/lib/utils/dataConversion';
import { zodResolver } from '@hookform/resolvers/zod';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';
import { SettingsContext } from '../SettingsContext';

const profileSchema = z.object({
  firstName: z.string().min(2, 'First name must be at least 2 characters.').max(50),
  lastName: z.string().min(2, 'Last name must be at least 2 characters.').max(50),
  alias: z.string().min(2, 'Alias must be at least 2 characters.').max(30).optional(),
  hobbies: z
    .array(
      z.object({
        label: z.string(),
        value: z.string(),
        category: z.string().optional(),
        disable: z.boolean().optional(),
      })
    )
    .max(5, 'You can select up to 5 hobbies.')
    .optional(),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export default function UserProfileCard() {
  const settingsContext = useContext(SettingsContext);
  const [loading, setLoading] = useState(false);

  const { fetchCurrentUser } = useAuth();

  const form = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      alias: '',
      hobbies: [],
    },
  });

  useEffect(() => {
    if (settingsContext?.settings) {
      form.reset({
        firstName: settingsContext.settings.firstName ?? '',
        lastName: settingsContext.settings.lastName ?? '',
        alias: settingsContext.settings.alias ?? '',
        hobbies: hobbiesById(settingsContext.settings.hobbies || []),
      });
    } else {
      form.reset({
        firstName: '',
        lastName: '',
        alias: '',
        hobbies: [],
      });
    }
  }, [settingsContext?.settings, form.reset]);

  if (!settingsContext || !settingsContext.settings) {
    return (
      <Card className="no-scrollbar flex h-[475px] w-full items-center justify-center border-none shadow-none">
        <MotionSpinner />
      </Card>
    );
  }

  const onSubmit = async (values: ProfileFormData) => {
    if (!settingsContext.settings) return;

    setLoading(true);
    try {
      await userService.updateProfileSettings({
        first_name: values.firstName,
        last_name: values.lastName,
        alias: values.alias,
        hobbies: values.hobbies?.map((hobby) => parseInt(hobby.value, 10)) || [],
      });
      await settingsContext.refreshSettings();
      await fetchCurrentUser();
      toast.success('Profile updated successfully');
    } catch (error) {
      toast.error('Failed to update profile');
      console.error('Error updating profile:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="no-scrollbar h-[475px] w-full overflow-y-auto border-none">
      <CardHeader>
        <CardTitle>Profile</CardTitle>
        <CardDescription>Edit your profile settings here.</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <CardContent>
            <div className="grid w-full items-center gap-4">
              <div className="flex gap-2">
                <FormField
                  control={form.control}
                  name="firstName"
                  render={({ field }) => (
                    <FormItem className="flex-1">
                      <FormLabel htmlFor="firstName">First Name</FormLabel>
                      <FormControl>
                        <Input id="firstName" placeholder="First Name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="lastName"
                  render={({ field }) => (
                    <FormItem className="flex-1">
                      <FormLabel htmlFor="lastName">Last Name</FormLabel>
                      <FormControl>
                        <Input id="lastName" placeholder="Last Name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              <div className="flex flex-col space-y-1.5">
                <FormLabel>Profile Picture</FormLabel>
                <ProfilePictureUploader
                  currentImage={settingsContext.settings.profilePicture ?? null}
                  onUploadSuccess={async () => {
                    await settingsContext.refreshSettings();
                    await fetchCurrentUser();
                  }}
                />
              </div>
              <FormField
                control={form.control}
                name="alias"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel htmlFor="alias">Alias</FormLabel>
                    <FormControl>
                      <Input id="alias" placeholder="Alias" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="hobbies"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Hobbies</FormLabel>
                    <FormControl>
                      <MultipleSelector
                        {...field}
                        placeholder="Select your hobbies..."
                        defaultOptions={HOBBIES}
                        groupBy="category"
                        hideClearAllButton={true}
                        maxSelected={5}
                        hidePlaceholderWhenSelected={true}
                        className="mb-4"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          </CardContent>
          <CardFooter className="mt-auto flex justify-end">
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
};
