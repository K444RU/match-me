import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MotionSpinner from './motion/MotionSpinner';
import InputField from './form_utilities/InputField';

const LoginForm = ({ setShowOverlay }: { setShowOverlay: (show: boolean) => void }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

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

    console.log(user);
    authenticate(user);
  };

  const authenticate = async (user: User) => {
    try {
      const res = await fetch('/api/users?');
      const data = await res.json();
      // TEMP:
      // we send username and password, use that on serverside to find user
      // and match password, but here we must temporarily find on client-side
      interface db_user {
        id: number;
        username: string;
        email: string;
        phone: string;
        password: string;
      }

      // TODO:
      // Show adequate UI display for incorrect password or username not
      // found
      const matchedUser = data.find(
        (db_user: db_user) => db_user.email === user.email
      );
      if (!matchedUser) {
        setLoading(false);
        console.error('No user found.');
        return;
      }
      if (matchedUser.password === user.password) {
        navigate('/chats');
        setShowOverlay(false);
      } else {
        console.error('Invalid Password');
      }
      //

      // PRODUCTION:
      // We send email and password to backend, if success: true, we return
      // a boolean of true. so if authenticate(user) === true, then login.
    } catch (error) {
      console.error('Error fetching data', error);
      throw error;
    }
    setLoading(false);
  };

  return (
    <form onSubmit={submitForm}>
      <InputField
        label="Email address"
        type="email"
        name="contact_email"
        placeholder="Enter email"
        value={email}
        onChange={setEmail}
        required={true}
      />
      <InputField
        label="Password"
        type="text"
        name="password"
        placeholder="Enter password"
        value={password}
        onChange={setPassword}
        required={true}
      />
      <button
        className="rounded-md bg-primary px-5 py-2 mt-3 text-text hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        {loading ? <MotionSpinner /> : 'Log in'}
      </button>
    </form>
  );
};

export default LoginForm;
