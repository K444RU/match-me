import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/authentication/AuthContext';
import LoginForm from './LoginForm';

const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  // If user is already logged in, redirect them
  useEffect(() => {
    if (user) {
      const from = location.state?.from?.pathname || '/chats';
      navigate(from, { replace: true });
    }
  }, [user, navigate, location]);

  return (
    <div className="flex h-screen items-center justify-center bg-background">
      <div className="w-full max-w-md rounded-md bg-accent-200 p-6">
        <h1 className="mb-6 text-center text-3xl font-bold text-text">Login</h1>
        <LoginForm />
      </div>
    </div>
  );
};

export default LoginPage;
