import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { PhoneInput } from '@/components/ui/phone-input';
import { useAuth } from '@/features/authentication';
import { userService } from '@/features/user';
import { zodResolver } from '@hookform/resolvers/zod';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { isValidPhoneNumber } from 'react-phone-number-input';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { z } from 'zod';
import { SettingsContext } from '../SettingsContext';

const accountSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address.' }),
  number: z.string().refine(isValidPhoneNumber, { message: 'Invalid phone number' }),
});

type AccountFormData = z.infer<typeof accountSchema>;

export default function UserAccountCard() {
  const settingsContext = useContext(SettingsContext);
  const [loading, setLoading] = useState(false);

  const { logout } = useAuth();
  const navigate = useNavigate();

  const form = useForm<AccountFormData>({
    resolver: zodResolver(accountSchema),
    defaultValues: {
      email: '',
      number: '',
    },
  });

  useEffect(() => {
    if (settingsContext?.settings) {
      form.reset({
        email: settingsContext.settings.email ?? '',
        number: settingsContext.settings.number ?? '',
      });
    } else {
      form.reset({
        email: '',
        number: '',
      });
    }
  }, [settingsContext?.settings, form.reset]);

  const onSubmit = async (values: AccountFormData) => {
    if (!settingsContext?.settings) return;

    setLoading(true);
    try {
      await userService.updateAccountSettings(values);

      if (settingsContext.settings.email !== values.email) {
        logout();
        navigate('/login');
        toast.info('Email updated. Please log in again.');
      } else {
        await settingsContext.refreshSettings();
        toast.success('Account updated successfully');
      }
    } catch (error) {
      toast.error('Failed to update account');
      console.error('Error updating account:', error);
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
    <Card className="h-[475px] w-full">
      <CardHeader>
        <CardTitle>Account</CardTitle>
        <CardDescription>Edit your account settings here.</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <CardContent>
              <div className="grid w-full items-center gap-4">
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="email">Email</FormLabel>
                      <FormControl>
                        <Input id="email" placeholder="Email" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="number"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel htmlFor="phoneNumber">Phone Number</FormLabel>
                      <FormControl>
                        <PhoneInput
                          id="phoneNumber"
                          placeholder="Enter a phone number"
                          defaultCountry="EE"
                          international
                          {...field}
                          className="w-full"
                        />
                      </FormControl>
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
};
