import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/authentication/AuthContext';
import RegisterForm from './RegisterForm';
import FormLayout from './FormLayout';

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
    <>
      <FormLayout title="Sign Up">
        <RegisterForm />
      </FormLayout>
    </>
  );
};

export default RegisterPage;
