import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { PhoneInput } from '@/components/ui/phone-input';
import { authService } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { isValidPhoneNumber, parsePhoneNumber, Value } from 'react-phone-number-input';
import { useNavigate } from 'react-router-dom';
import { z } from 'zod';
import FormResponse from './FormResponse';

const registerSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address.' }),
  password: z.string().min(6, { message: 'Password must contain at least 6 characters.' }),
  number: z.string().refine(isValidPhoneNumber, { message: 'Invalid phone number' }),
});

export default function RegisterForm() {
  const [loading, setLoading] = useState(false);
  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');
  const [resState, setResState] = useState<'error' | 'success'>('error');

  const navigate = useNavigate();

  const form = useForm<z.infer<typeof registerSchema>>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      email: '',
      password: '',
      number: '',
    },
  });

  const onSubmit = async (values: z.infer<typeof registerSchema>) => {
    setLoading(true);
    setResTitle('');
    setResSubtitle('');

    authService
      .register(values)
      .then((_res) => {
        setResState('success');
        setResTitle('Nice! You have been registered.');
        setResSubtitle('Please verify your email.');
        form.reset();
      })
      .catch((err) => {
        console.error('Error registering user:', err);
        setResState('error');

        if (err.code === 'ERR_NETWORK') {
          setResTitle('Connection Error');
          setResSubtitle('Unable to connect to the server. Please check your internet connection and try again.');
          return;
        }

        if (err.response) {
          if (err.response.status === 401) {
            setResTitle('Registration Failed');
            setResSubtitle('Could not complete registration.');
          } else if (err.response.status === 400 || err.response.status === 409) {
            setResTitle('Registration Failed');
            let errorMessages = '';
            if (typeof err.response.data === 'object' && err.response.data !== null) {
              Object.keys(err.response.data).forEach((key) => {
                // Append with a newline if not the first message
                errorMessages += (errorMessages ? '\n' : '') + err.response.data[key];
              });
            } else if (typeof err.response.data === 'string') {
              // Handle case where backend sends a single string error
              errorMessages = err.response.data;
            } else {
              // Fallback if the error format is unexpected
              errorMessages = 'An unexpected validation error occurred.';
            }
            setResSubtitle(errorMessages);
          } else {
            setResTitle('Something went wrong...');
            setResSubtitle('Please contact support.');
          }
        } else {
          // Handle cases where err.response is undefined
          setResTitle('Error');
          setResSubtitle('An unexpected error occurred. Please try again.');
        }
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col items-center gap-2">
        {resTitle && resSubtitle && <FormResponse title={resTitle} subtitle={resSubtitle} state={resState} />}

        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem className="w-full">
              <FormControl>
                <Input type="email" placeholder="Email" autoComplete="email" {...field} className="bg-background" />
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
                <Input type="password" placeholder="Password" autoComplete="new-password" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="number"
          render={({ field }) => (
            <FormItem className="w-full">
              <FormControl>
                <PhoneInput
                  placeholder="Enter a phone number"
                  autoComplete="tel"
                  defaultCountry="EE"
                  international
                  {...field}
                  className="w-full rounded-lg"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <Button
          disabled={loading}
          className="flex w-full items-center justify-center gap-2 rounded-md font-semibold tracking-wide"
          type="submit"
          aria-label="Submit form."
        >
          <span>Register</span>
          {loading && <MotionSpinner />}
        </Button>
      </form>
    </Form>
  );
}
