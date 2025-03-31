import { getMeController } from '@/api/me-controller';
import type { CurrentUserResponseDTO } from '@/api/types';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import axios from 'axios';
import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../../components/ui/forms/InputField';
import FormResponse from './FormResponse';

const testUsers = [
  { email: 'test1@example.com', password: '123456' },
  { email: 'test2@example.com', password: '123456' },
  { email: 'test3@example.com', password: '123456' },
  { email: 'test4@example.com', password: '123456' },
];

export default function LoginForm() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const { getCurrentUser } = getMeController();
  const from = location.state?.from?.pathname || '/chats';

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');

  const handleTestUser = (email: string, password: string) => {
    setEmail(email);
    setPassword(password);
  };

  const submitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await login({ email, password });

      if (response.data.token) {
        localStorage.setItem('authToken', response.data.token);

        const token = localStorage.getItem('authToken');
        if (!token) {
          setResTitle('Token Error');
          setResSubtitle('Authentication token was not stored correctly.');
          return;
        }

        const currentUserResponse = await getCurrentUser();

        const currentUser = currentUserResponse.data;
        const requiredFields: (keyof CurrentUserResponseDTO)[] = ['firstName', 'lastName', 'alias', 'email'];
        const isProfileIncomplete = requiredFields.some((field) => !currentUser[field]);

        navigate(isProfileIncomplete ? '/profile-completion' : from === '/logout' ? '/chats' : from, { replace: true });
      }
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        if (err.response?.status === 401) {
          setResTitle('Invalid Credentials');
          setResSubtitle('Please check your email and password');
        } else {
          setResTitle('Login Failed');
          setResSubtitle('An unexpected error occurred. Please try again.');
        }
      } else {
        setResTitle('Login Failed');
        setResSubtitle('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
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
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        <span>Login</span>
        {loading && <MotionSpinner />}
      </Button>

      <div className="mt-4 flex w-full flex-col gap-2">
        <div className="grid grid-cols-2 gap-2">
          {testUsers.map((user) => (
            <Button
              type="button"
              className="rounded bg-gray-200 px-3 py-1 text-sm"
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
