import { useState } from 'react';
import InputField from '../../../components/ui/InputField';
import MotionSpinner from '@animations/MotionSpinner';
import FormResponse from './FormResponse';
import { register } from '@services/AuthService';

const RegisterForm = () => {
  const [email, setEmail] = useState('admin@kood.tech');
  const [phone, setPhone] = useState('5341449');
  const [countryCode, setCountryCode] = useState('+372');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');
  const [resState, setResState] = useState<'error' | 'success'>('error');

  const submitForm = (e: any) => {
    e.preventDefault();
    setLoading(true);

    register(email, `${countryCode} ${phone}`, password)
      .then((res) => {
        // TODO: Don't redirect on register & wait for email verify.
        // This current approach would cause a unnecessary
        // waste of resources (unverified accounts in DB)
        // So on successful registration we indicate 'you have been registered'
        // and then under that 'Please verify your email.'
        // Email verification allows login and redirects to /profile
        // /profile should be smarter, instead of using /finish-profile we just use the main profile page
        // if user has missing fields, he cannot proceed after fields are finished.
        // otherwise it is just the normal /profile page.
        // navigate('/profile');
        // setShowOverlay(false);
        console.log(res);
        setResState('success');
        setResTitle('Nice! You have been registered.');
        setResSubtitle('Please verify your email.');
      })
      .catch((err) => {
        console.error('Error registering user:', err);
        if (err.response.status === 401) {
          setResTitle('Wrong Credentials');
          setResSubtitle('Invalid username or password');
        } else if (err.response.status === 400) {
          setResTitle('We found some errors');
          setResState('error');
          setResSubtitle('');
          // TODO: Make back-end return better error messages
          // size must be between 0 and 20 -> what size? (its number...)
          // Password must be between 6 and 40 characters -> good
          // Error: email is already taken!
          Object.keys(err.response.data).forEach((key) => {
            setResSubtitle((prev) => `${prev}\n${err.response.data[key]}`);
          });
        } else {
          setResTitle('Something went wrong...');
          setResSubtitle('Please contact support.');
        }
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <form onSubmit={submitForm} className="flex flex-col items-center gap-2">
      {resTitle && resSubtitle && (
        <FormResponse
          title={resTitle}
          subtitle={resSubtitle}
          state={resState}
        />
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
      <div className="flex w-full space-x-2">
        <InputField
          className="w-24"
          // label="Country"
          type="text"
          name="country_code"
          placeholder="+372"
          value={countryCode}
          onChange={setCountryCode}
          required={true}
        />
        <InputField
          // label="Phone number"
          type="text"
          name="contact_phone"
          placeholder="Phone"
          value={phone}
          onChange={setPhone}
          required={true}
        />
      </div>
      <button
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        <span>Register</span>
        {loading && <MotionSpinner />}
      </button>
    </form>
  );
};

export default RegisterForm;
