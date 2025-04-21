import { useAuth } from '@/features/authentication';
import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import FormLayout from './FormLayout';
import RegisterForm from './RegisterForm';

export default function RegisterPage() {
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
    <FormLayout title="Sign Up">
      <RegisterForm />
    </FormLayout>
  );
};
