import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { PhoneInput } from '@/components/ui/phone-input';
import { authService } from '@/features/authentication';
import MotionSpinner from '@animations/MotionSpinner';
import { useState } from 'react';
import { isValidPhoneNumber, parsePhoneNumber, Value } from 'react-phone-number-input';
import { useNavigate } from 'react-router-dom';
import FormResponse from './FormResponse';

export default function RegisterForm() {
  const [email, setEmail] = useState('');
  const [number, setNumber] = useState<Value | undefined>(undefined);
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
  const handleNumberChange = (val: Value) => {
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
        number: number?.toString() ?? '',
        password,
      })
      .then((_res) => {
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

      <Input
        type="email"
        name="contact_email"
        placeholder="Email"
        autoComplete="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
        className="bg-background"
      />

      <Input
        type="password"
        name="password"
        autoComplete="new-password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
        className="bg-background"
      />

      <div className="flex w-full space-x-2">
        <PhoneInput
          value={number}
          defaultCountry="EE"
          placeholder="Enter a phone number"
          autoComplete="tel"
          onChange={handleNumberChange}
          className="w-full rounded-lg"
        />
      </div>
      {numberError && <p className="text-sm text-red-500"> {numberError}</p>}

      <Button
        className="flex w-full items-center justify-center gap-2 rounded-md font-semibold tracking-wide"
        type="submit"
        aria-label="Submit form."
      >
        <span>Register</span>
        {loading && <MotionSpinner />}
      </Button>
    </form>
  );
};