import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import InputField from './form_utilities/InputField';
import MotionSpinner from './motion/MotionSpinner';
import axios from 'axios';

const RegisterForm = ({
  setShowOverlay,
}: {
  setShowOverlay: (show: boolean) => void;
}) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@kood.tech');
  const [phone, setPhone] = useState('5341449');
  const [countryCode, setCountryCode] = useState('+372');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);

  interface User {
    email: string;
    phone: string;
    password: string;
  }
  const submitForm = (e: any) => {
    e.preventDefault();
    setLoading(true);

    const newUser: User = {
      email,
      phone: `${countryCode} ${phone}`,
      password,
    };

    axios.post('/api/auth/signup', {
      email: newUser.email,
      number: newUser.phone,
      password: newUser.password,
    })
    .then((res) => {
      navigate('/finish-profile');
      setShowOverlay(false);
      console.log(res);
    })
    .catch((err) => {
      console.error('Error registering user:', err);
    }).finally(() => {
      setLoading(false);
    });
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
        className="rounded-md bg-primary px-5 py-2 text-text hover:bg-primary-200 hover:text-text flex items-center justify-center min-w-[120px]"
        type="submit"
        aria-label="Submit form and close overlay."
      >
        <span className={loading ? 'mr-2' : ''}>Register</span>
        {loading && <MotionSpinner/>}
      </button>
    </form>
  );
};

export default RegisterForm;
