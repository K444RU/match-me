import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import MotionSpinner from '@animations/MotionSpinner';
import InputField from '../../../components/ui/forms/InputField';
import { useAuth } from '@/features/authentication/AuthContext';
import FormResponse from './FormResponse';

const LoginForm = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const from = location.state?.from?.pathname || '/chats';
  const [email, setEmail] = useState('admin@kood.tech');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');

  const submitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    console.log('🚀 LoginForm: Submitting with:', { email, password });

    try {
      const response = await login(email, password);
      console.log('✅ LoginForm: Login successful:', response);
      if (response.data.token) {
        // Send them back to the page they tried to visit when they were
        // redirected to the login page. Use { replace: true } so we don't create
        // another entry in the history stack for the login page.  This means that
        // when they get to the protected page and click the back button, they
        // won't end up back on the login page, which is also really nice for the
        // user experience.
        navigate(from, { replace: true });
      }
    } catch (err: any) {
      console.error('❌ LoginForm: Login failed:', err);
      if (err?.response?.status === 401) {
        setResTitle('Invalid Credentials');
        setResSubtitle('Please check your email and password');
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
      {resTitle && resSubtitle && (
        <FormResponse title={resTitle} subtitle={resSubtitle} />
      )}
      <InputField
        // label="Email address"
        type="email"
        name="contact_email"
        placeholder="Email"
        value={email}
        onChange={setEmail}
        required={true}
      />
      <InputField
        // label="Password"
        type="password"
        name="password"
        placeholder="Password"
        value={password}
        onChange={setPassword}
        required={true}
      />
      <button
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        <span>Login</span>
        {loading && <MotionSpinner />}
      </button>
    </form>
  );
};

export default LoginForm;
