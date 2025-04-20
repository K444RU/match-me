import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PhoneInput } from '@/components/ui/phone-input';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuth } from '@/features/authentication';
import { userService } from '@/features/user';
import { zodResolver } from '@hookform/resolvers/zod';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { isValidPhoneNumber, parsePhoneNumber } from 'react-phone-number-input';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { z } from 'zod';
import { SettingsContext } from '../SettingsContext';

const accountSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address.' }),
  number: z.string().refine(isValidPhoneNumber, { message: 'Invalid phone number' }),
});

const UserAccountCard = () => {
  const settingsContext = useContext(SettingsContext);
  const [loading, setLoading] = useState(false);

  const { logout } = useAuth();
  const navigate = useNavigate();

  const form = useForm<z.infer<typeof accountSchema>>({
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

  const onSubmit = async (values: z.infer<typeof accountSchema>) => {
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

  const showSkeletons = !settingsContext?.settings;

  return (
    <Card className="h-[475px] w-full border-none shadow-none">
      <CardHeader>
        <CardTitle>Account</CardTitle>
        <CardDescription>Edit your account settings here.</CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {showSkeletons ? (
              <div className="grid w-full items-center gap-4">
                <div className="flex flex-col space-y-1.5">
                  <Skeleton className="mb-1 h-5 w-10" />
                  <Skeleton className="h-[40px] w-full" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Skeleton className="mb-1 h-5 w-24" />
                  <Skeleton className="h-[40px] w-full" />
                </div>
              </div>
            ) : (
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
            )}
            <CardFooter className="flex justify-end">
              <Button type="submit" disabled={loading || showSkeletons}>
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
      </CardContent>
    </Card>
  );
};

export default UserAccountCard;
