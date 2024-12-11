import { useState } from 'react';
import InputField from '../../../components/ui/forms/InputField';
import MotionSpinner from '@animations/MotionSpinner';
import FormResponse from './FormResponse';
import { register } from '@/features/authentication/services/AuthService';
import PhoneInput from './PhoneInput';
import {useNavigate} from "react-router-dom";

const RegisterForm = () => {
    const [email, setEmail] = useState('admin@kood.tech');
    const [phone, setPhone] = useState('5341449');
    const [countryCode, setCountryCode] = useState('+372');
    const [password, setPassword] = useState('123456');
    const [loading, setLoading] = useState(false);
    const [resTitle, setResTitle] = useState('');
    const [resSubtitle, setResSubtitle] = useState('');
    const [resState, setResState] = useState<'error' | 'success'>('error');

    const navigate = useNavigate();

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

                navigate('/profile-completion');
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
        <form onSubmit={submitForm} className="flex flex-col items-center gap-4">
            {resTitle && resSubtitle && (
                <FormResponse title={resTitle} subtitle={resSubtitle} state={resState}/>
            )}

            <div className="flex flex-col w-full">
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

            <div className="flex flex-col w-full">
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

            <div className="flex flex-col w-full">
                <label
                    htmlFor="phone"
                    className="mb-1 text-sm font-medium text-gray-700"
                >
                    Country Code & Phone Number
                </label>
                <div className="flex space-x-2">
                    <PhoneInput
                        countryCode={countryCode}
                        phone={phone}
                        onCountryCodeChange={setCountryCode}
                        onPhoneChange={setPhone}
                    />
                </div>
            </div>

            <button
                className="flex w-full items-center justify-center gap-2 rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-white transition-colors hover:bg-primary-200"
                type="submit"
                aria-label="Submit form."
            >
                <span>Register</span>
                {loading && <MotionSpinner/>}
            </button>
        </form>
    );
};

export default RegisterForm;
