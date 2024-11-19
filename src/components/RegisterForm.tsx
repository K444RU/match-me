import { useNavigate } from 'react-router-dom';
import { useState } from 'react';

const RegisterForm = ({
  setShowOverlay,
}: {
  setShowOverlay: (show: boolean) => void;
}) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [countryCode, setCountryCode] = useState('');
  const [password, setPassword] = useState('');

  interface User {
    email: string;
    phone: string;
    password: string;
  }
  const submitForm = (e: any) => {
    e.preventDefault();

    const newUser: User = {
      email,
      phone: `${countryCode} ${phone}`,
      password,
    };

    registerUser(newUser);
  };

  // TODO:
  // Will have to rethink error logic here once backend server implemented.
  const registerUser = async (newUser: User) => {
    try {
      const res = await fetch('/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newUser),
      });
      if (!res.ok) {
        console.error('Error registering user:', res);
        return;
      }
      console.log(res);
    } catch (error) {
      console.error('Error registering user:', error);
      return;
    }
    navigate('/finish-profile');
    setShowOverlay(false);
    return;
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
      <div className="mb-3 flex place-items-start space-x-2">
        <div className="place-items-start">
          <label className="ml-2" htmlFor="country_code">
            Country
          </label>
          <div className="rounded-md border border-primary bg-text-100 p-1">
            <input
              id="country_code"
              name="country_code"
              className="w-20 bg-text-100"
              placeholder="+372"
              value={countryCode}
              onChange={(e) => setCountryCode(e.target.value)}
            ></input>
          </div>
        </div>
        <div className="place-items-start">
          <label className="ml-2" htmlFor="contact_phone">
            Phone number
          </label>
          <div className="rounded-md border border-primary bg-text-100 p-1">
            <input
              type="tel"
              id="contact_phone"
              name="contact_phone"
              className="bg-text-100"
              placeholder="Enter phone number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
          </div>
        </div>
      </div>
      <button
        className="rounded-md bg-primary px-5 py-2 text-text hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form and close overlay."
      >
        Register
      </button>
    </form>
  );
};

export default RegisterForm;
