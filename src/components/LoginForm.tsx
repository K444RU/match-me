import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from "motion/react"

const LoginForm = ({ closeOverlay }: { closeOverlay: () => void }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  interface User {
    email: string;
    password: string;
  }

  const submitForm = (e: any) => {
    e.preventDefault();

    const user: User = {
      email,
      password,
    };

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

      const matchedUser = data.find(
        (db_user: db_user) => db_user.email === user.email
      );
      if (matchedUser.password === user.password) {
        closeOverlay();
      }
      //

      // PRODUCTION:
      // We send email and password to backend, if success: true, we return
      // a boolean of true. so if authenticate(user) === true, then login.
      navigate('/chats')
    } catch (error) {
      console.error('Error fetching data', error);
      throw error;
    }
  };

  return (
    <form onSubmit={submitForm}>
      <div className="mb-3 place-items-start">
        <label className="ml-2" htmlFor="contact_email">
          Email Address
        </label>
        <div className="rounded-md border border-primary bg-text-100 p-1">
          <input
            type="email"
            id="contact_email"
            name="contact_email"
            className="bg-text-100"
            placeholder="Enter email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
      </div>
      <div className="mb-3 place-items-start">
        <label className="ml-2" htmlFor="password">
          Password
        </label>
        <div className="rounded-md border border-primary bg-text-100 p-1">
          <input
            type="text"
            id="password"
            name="password"
            className="bg-text-100"
            placeholder="Enter password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
      </div>
      <button
        className="rounded-md bg-primary px-5 py-2 text-text hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        Register
      </button>
    </form>
  );
};

export default LoginForm;
