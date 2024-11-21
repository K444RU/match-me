import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MotionSpinner from '@animations/MotionSpinner';
import InputField from '../../../components/ui/InputField';
import axios from 'axios';
import FormResponse from './FormResponse';

const LoginForm = ({
  setShowOverlay,
}: {
  setShowOverlay: (show: boolean) => void;
}) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@kood.tech');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');

  interface User {
    email: string;
    password: string;
  }

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    const user: User = {
      email,
      password,
    };

    axios
      .post('/api/auth/signin', {
        email: user.email,
        password: user.password,
      })
      .then((res) => {
        if (res.data.token) {
          localStorage.setItem('user', JSON.stringify(res.data));
        }
        console.log(res);
        navigate('/chats');
        setShowOverlay(false);
      })
      .catch((err) => {
        if (err.response.status === 401) {
          setResTitle('Wrong Credentials');
          setResSubtitle('Invalid username or password');
        } else {
          setResTitle('Something went wrong...');
          setResSubtitle('Please contact support.');
        }
        console.error('Error logging in:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <form onSubmit={submitForm} className="flex flex-col items-center gap-3">
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
        type="text"
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
