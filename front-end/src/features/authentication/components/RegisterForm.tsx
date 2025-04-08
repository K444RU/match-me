import { authService } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import { CountryCodePhoneInput } from '@ui/country-code-phone-input';
import { useState } from 'react';
import { isValidPhoneNumber, parsePhoneNumber } from 'react-phone-number-input';
import { useNavigate } from 'react-router-dom';
import InputField from '../../../components/ui/forms/InputField';
import FormResponse from './FormResponse';

const RegisterForm = () => {
  const [email, setEmail] = useState('');
  const [number, setNumber] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const [numberError, setNumberError] = useState('');

  const [resTitle, setResTitle] = useState('');
  const [resSubtitle, setResSubtitle] = useState('');
  const [resState, setResState] = useState<'error' | 'success'>('error');

  const navigate = useNavigate();

  /**
     * Updates the phone number state and performs validation when the user changes the phone number input.
     *
     * What It Does:
     * - Takes the phone number value entered by the user (expected in E.164 format).
     * - Saves the full phone number (`number`) state.
     * - Validates the number using `isValidPhoneNumber` from `react-phone-number-input`.
     * - Sets or clears the `numberError` state based on the validation result.

     * Why the Full `number` is Used:
     * - `number` stores the complete E.164 formatted number (e.g., "+37255501444"), which is the standard format needed for backend systems and reliable international validation.

     * Example:
     * If the user enters "+37255501444":
     * - `number` state is set to "+37255501444".
     * - If it's valid according to `isValidPhoneNumber`, `numberError` is cleared. Otherwise, an error message is set.
     *
     * This approach ensures the number is validated on the front-end and sent in the correct format to the backend.
     */
  const handleNumberChange = (val: string | undefined) => {
    const E164Number = val ?? '';
    setNumber(E164Number);

    let validationError = '';

    if (E164Number) {
      const parsedPhoneNumber = parsePhoneNumber(E164Number);
      if (parsedPhoneNumber && isValidPhoneNumber(E164Number)) {
        validationError = '';
      } else {
        validationError = 'Invalid phone number';
      }
    }

    setNumberError(validationError);
  };

  const submitForm = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (numberError) {
      alert('Please enter a valid phone number before submitting.');
      return;
    }

    setLoading(true);

    authService
      .register({
        email,
        number,
        password,
      })
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
        navigate('/login');
      })
      .catch((err) => {
        console.error('Error registering user:', err);
        setResState('error');

        // Handle network errors
        if (err.code === 'ERR_NETWORK') {
          setResTitle('Connection Error');
          setResSubtitle('Unable to connect to the server. Please check your internet connection and try again.');
          return;
        }

        if (err.response) {
          if (err.response.status === 401) {
            setResTitle('Wrong Credentials');
            setResSubtitle('Invalid username or password');
          } else if (err.response.status === 400) {
            setResTitle('We found some errors');
            let errorMessages = '';
            if (typeof err.response.data === 'object' && err.response.data !== null) {
              Object.keys(err.response.data).forEach((key) => {
                // Append with a newline if not the first message
                errorMessages += (errorMessages ? '\n' : '') + err.response.data[key];
              });
            } else if (typeof err.response.data === 'string') {
              // Handle case where backend sends a single string error
              errorMessages = err.response.data;
            } else {
              // Fallback if the error format is unexpected
              errorMessages = 'An unexpected validation error occurred.';
            }
            setResSubtitle(errorMessages);
          } else {
            setResTitle('Something went wrong...');
            setResSubtitle('Please contact support.');
          }
        } else {
          // Handle cases where err.response is undefined
          setResTitle('Error');
          setResSubtitle('An unexpected error occurred. Please try again.');
        }
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <form onSubmit={submitForm} className="flex flex-col items-center gap-4">
      {resTitle && resSubtitle && <FormResponse title={resTitle} subtitle={resSubtitle} state={resState} />}

      <div className="flex w-full flex-col">
        <label htmlFor="contact_email" className="mb-1 text-sm font-medium text-gray-700">
          Email
        </label>
        <InputField
          type="email"
          name="contact_email"
          placeholder="Email"
          value={email}
          onChange={setEmail}
          required={true}
        />
      </div>

      <div className="flex w-full flex-col">
        <label htmlFor="password" className="mb-1 text-sm font-medium text-gray-700">
          Password
        </label>
        <InputField
          type="password"
          name="password"
          placeholder="Password"
          value={password}
          onChange={setPassword}
          required={true}
        />
      </div>

      <div className="flex w-full flex-col">
        <label htmlFor="phone2" className="mb-1 text-sm font-medium text-gray-700">
          Country Code & Phone Number
        </label>
        <div className="flex w-full space-x-2">
          <CountryCodePhoneInput
            value={number}
            defaultCountry="EE"
            placeholder="Enter a phone number"
            onChange={handleNumberChange}
            className="w-full"
          />
        </div>
        {numberError && <p className="text-sm text-red-500"> {numberError}</p>}
      </div>

      <button
        className="flex w-full items-center justify-center gap-2 rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-white transition-colors hover:bg-primary-200"
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
