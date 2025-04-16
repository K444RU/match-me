import { Button } from '@/components/ui/button';
import { useAuth } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import React, { useState } from 'react';
import InputField from '../../../components/ui/forms/InputField';
import FormResponse from './FormResponse';

const testUsers = [
  { email: 'john.doe@example.com', password: '123456' },
  { email: 'jane.smith@example.com', password: '123456' },
  { email: 'alice.johnson@example.com', password: '123456' },
  { email: 'toomas.saar@example.com', password: '123456' },
  { email: 'madis.paidest@example.com', password: '123456' },
  { email: 'invalid@example.com', password: '123456' },
];

export default function LoginForm() {
  const { login, isLoading } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');

  const handleTestUser = (email: string, password: string) => {
    setEmail(email);
    setPassword(password);
  };

  const submitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    setResTitle('');
    setResSubtitle('');

    const result = await login({ email, password });

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
      setPassword('');
    }
    console.debug('LoginForm: submitForm finished processing result.');
  };

  return (
    <form onSubmit={submitForm} className="flex flex-col items-center gap-2">
      {resTitle && resSubtitle && <FormResponse title={resTitle} subtitle={resSubtitle} />}
      <InputField type="email" name="contact_email" placeholder="Email" value={email} onChange={setEmail} required />
      <InputField
        type="password"
        name="password"
        placeholder="Password"
        value={password}
        onChange={setPassword}
        required
      />
      <Button
        disabled={isLoading}
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
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
              className="rounded-sm bg-gray-200 px-3 py-1 text-sm"
              onClick={() => handleTestUser(user.email, user.password)}
              key={user.email}
            >
              {user.email}
            </Button>
          ))}
        </div>
      </div>
    </form>
  );
}
