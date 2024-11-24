import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/authentication/AuthContext';
import RegisterForm from './RegisterForm';

const RegisterPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  // If user is already logged in, redirect them
  useEffect(() => {
    if (user) {
      navigate('/profile', { replace: true });
    }
  }, [user, navigate, location]);

  return (
    <div className="flex h-screen items-center justify-center bg-background">
      <div className="w-full max-w-md rounded-md bg-accent-200 p-6">
        <h1 className="mb-6 text-center text-3xl font-bold text-text">
          Sign Up
        </h1>
        <RegisterForm />
      </div>
    </div>
  );
};

export default RegisterPage;
