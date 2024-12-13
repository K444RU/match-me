import {
    createBrowserRouter,
    createRoutesFromElements,
    Route,
} from 'react-router-dom';
import MainLayout from '../layout/MainLayout';
import HomePage from '../pages/HomePage';
import ChatsPage from '../pages/chats/ChatsPage';
import SettingsPage from '../pages/SettingsPage';
import {AuthenticationGuard} from './components/AuthenticationGuard';
import {useAuth} from '@/features/authentication/AuthContext';
import {useEffect} from 'react';
import LoginPage from '@/features/authentication/components/LoginPage';
import RegisterPage from '@/features/authentication/components/RegisterPage';
import ProfileCompletionPage from '@/pages/profile-completion/ProfileCompletionPage';
import AuthenticatedLayout from '@/layout/AuthenticatedLayout';

const LogoutPage = () => {
    const {user, logout} = useAuth();

    useEffect(() => {
        if (user) logout();
    }, [user, logout]);
    return null;
};

export const routes = createRoutesFromElements(
    <Route>
        {/* Public routes with main layout */}
        <Route element={<MainLayout/>}>
            <Route index element={<HomePage/>}/>

            {/* Login page in case unauthenticated */}
            <Route element={<AuthenticationGuard guardType="unauthenticated"/>}>
                <Route path="login" element={<LoginPage/>}/>
                <Route path="register" element={<RegisterPage/>}/>
            </Route>
        </Route>

            {/* Protected Routes */}
            <Route element={<AuthenticatedLayout />}>
                <Route element={<AuthenticationGuard/>}>
                    <Route path="settings" element={<SettingsPage/>}/>
                    <Route path="profile-completion" element={<ProfileCompletionPage/>}/>
                    <Route path="chats" element={<ChatsPage/>}/>
                    <Route path="logout" element={<LogoutPage/>}/>
                </Route>
            </Route>
    </Route>
);

export const router = createBrowserRouter(routes);
