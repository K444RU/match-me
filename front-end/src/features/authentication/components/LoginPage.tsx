import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/authentication';
import LoginForm from './LoginForm';
import FormLayout from './FormLayout';

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
        <>
            <FormLayout title="Log in">
                <LoginForm />
            </FormLayout>
        </>
    );
};

export default LoginPage;
