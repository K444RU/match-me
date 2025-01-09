import { useState } from 'react';
import InputField from '../../../components/ui/forms/InputField';
import MotionSpinner from '@animations/MotionSpinner';
import FormResponse from './FormResponse';
import { authService } from '@/features/authentication';
import { useNavigate } from 'react-router-dom';
import { CountryCodePhoneInput } from '@ui/country-code-phone-input';
import { parsePhoneNumber } from 'react-phone-number-input';

const RegisterForm = () => {
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [localNumber, setLocalNumber] = useState('');
    const [countryCode, setCountryCode] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [resTitle, setResTitle] = useState('');
    const [resSubtitle, setResSubtitle] = useState('');
    const [resState, setResState] = useState<'error' | 'success'>('error');

    const navigate = useNavigate();

    /**
     * Updates phone-related information when the user changes the phone number input.
     *
     * What It Does:
     * - Takes the phone number the user enters.
     * - Saves the full phone number (`phone`) for backend use.
     * - Splits the number into:
     *   - `countryCode` (e.g., "+372" for the EE).
     *   - `localNumber` (the rest of the number after the country code).
     * - If the phone number is invalid or empty, it clears the country code and local number.

     * Why to Use `phone` and `localNumber`:
     * - `phone` is the complete number, needed for backend systems and international use.
     * - `localNumber` is just the local part of the number, useful for showing or using it in user-friendly ways.

     * Example:
     * If the user enters "+37255501444":
     * - `phone` is "+37255501444".
     * - `countryCode` is "+372".
     * - `localNumber` is "55501444".
     *
     * This separation helps make the app work smoothly for both backend systems and user-facing features.
     */
    const handlePhoneChange = (val: string | undefined) => {
        const E164Number = val ?? '';
        setPhone(E164Number);

        let countryCode = '';
        let localNumber = '';

        if (E164Number) {
            const parsedPhoneNumber = parsePhoneNumber(E164Number);
            if (parsedPhoneNumber) {
                countryCode = '+' + parsedPhoneNumber.countryCallingCode;
                localNumber = parsedPhoneNumber.nationalNumber;
            }
        }

        setCountryCode(countryCode);
        setLocalNumber(localNumber);
    };

    const submitForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);

        authService
            .register({
                email,
                number: `${countryCode} ${localNumber}`,
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
                navigate('/profile-completion');
            })
            .catch((err) => {
                console.error('Error registering user:', err);
                setResState('error');

                // Handle network errors
                if (err.code === 'ERR_NETWORK') {
                    setResTitle('Connection Error');
                    setResSubtitle(
                        'Unable to connect to the server. Please check your internet connection and try again.'
                    );
                    return;
                }

                if (err.response.status === 401) {
                    setResTitle('Wrong Credentials');
                    setResSubtitle('Invalid username or password');
                } else if (err.response.status === 400) {
                    setResTitle('We found some errors');
                    setResSubtitle('');
                    // TODO: Make back-end return better error messages
                    // size must be between 0 and 20 -> what size? (its number...)
                    // Password must be between 6 and 40 characters -> good
                    // Error: email is already taken!
                    Object.keys(err.response.data).forEach((key) => {
                        setResSubtitle(
                            (prev) => `${prev}\n${err.response.data[key]}`
                        );
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
        <form
            onSubmit={submitForm}
            className="flex flex-col items-center gap-4"
        >
            {resTitle && resSubtitle && (
                <FormResponse
                    title={resTitle}
                    subtitle={resSubtitle}
                    state={resState}
                />
            )}

            <div className="flex w-full flex-col">
                <label
                    htmlFor="contact_email"
                    className="mb-1 text-sm font-medium text-gray-700"
                >
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
                <label
                    htmlFor="password"
                    className="mb-1 text-sm font-medium text-gray-700"
                >
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
                <label
                    htmlFor="phone2"
                    className="mb-1 text-sm font-medium text-gray-700"
                >
                    Country Code & Phone Number
                </label>
                <div className="flex w-full space-x-2">
                    <CountryCodePhoneInput
                        value={phone}
                        defaultCountry="EE"
                        placeholder="Enter a phone number"
                        onChange={handlePhoneChange}
                        className="w-full"
                    />
                </div>
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
