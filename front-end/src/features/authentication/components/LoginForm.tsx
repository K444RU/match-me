import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import FormResponse from './FormResponse';

const testUsers = [
  { email: 'john.doe@example.com', password: '123456' },
  { email: 'jane.smith@example.com', password: '123456' },
  { email: 'alice.johnson@example.com', password: '123456' },
  { email: 'toomas.saar@example.com', password: '123456' },
  { email: 'madis.paidest@example.com', password: '123456' },
  { email: 'invalid@example.com', password: '123456' },
];

const loginSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address.' }),
  password: z.string().min(6, { message: 'Password must contain at least 6 characters.' }),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginForm() {
  const { login, isLoading } = useAuth();

  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');

  const form = useForm<LoginFormData>({ resolver: zodResolver(loginSchema) });

  const handleTestUser = (email: string, password: string) => {
    form.setValue('email', email);
    form.setValue('password', password);
    setResTitle('');
    setResSubtitle('');
  };

  const onSubmit = async (values: LoginFormData) => {
    setResTitle('');
    setResSubtitle('');

    const result = await login(values);

    if (result.success && result.user) {
      console.debug(`LoginForm: Login successful via context result.`);
    } else {
      console.warn(`LoginForm: Login failed. Reason: ${result.error?.title} - ${result.error?.subtitle}`);
      if (result.error) {
        setResTitle(result.error.title);
        setResSubtitle(result.error.subtitle);
      } else {
        setResTitle('Login Failed');
        setResSubtitle('An unexpected error occurred.');
      }
      form.setValue('password', '');
    }
    console.debug('LoginForm: submitForm finished processing result.');
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col items-center gap-2">
        {resTitle && resSubtitle && <FormResponse title={resTitle} subtitle={resSubtitle} />}
        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem className="w-full">
              <FormControl>
                <Input type="email" placeholder="Email" autoComplete="email" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="password"
          render={({ field }) => (
            <FormItem className="w-full">
              <FormControl>
                <Input type="password" placeholder="Password" autoComplete="current-password" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button
          disabled={isLoading}
          className="flex w-full items-center justify-center gap-2 font-semibold tracking-wide"
          type="submit"
          aria-label="Submit form."
        >
          <span>Login</span>
          {isLoading && <MotionSpinner />}
        </Button>

        <div className="mt-4 flex w-full flex-col gap-2">
          <div className="grid grid-cols-2 gap-2">
            {testUsers.map((user) => (
              <Button
                type="button"
                className="rounded-sm px-3 py-1 text-sm"
                variant="secondary"
                onClick={() => handleTestUser(user.email, user.password)}
                key={user.email}
                disabled={isLoading}
              >
                {user.email.split('@')[0]}
              </Button>
            ))}
          </div>
        </div>
      </form>
    </Form>
  );
}
