import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import InputField from './form_utilities/InputField';

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
      <div className="mb-3 flex place-items-start space-x-2">
        <InputField className="w-20"
        label="Country"
        type="text"
        name="country_code"
        placeholder="+372"
        value={countryCode}
        onChange={setCountryCode}
        required={true}
        />
        <InputField
        label="Phone number"
        type="text"
        name="contact_phone"
        placeholder="Enter phone number"
        value={phone}
        onChange={setPhone}
        required={true}
        />
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
