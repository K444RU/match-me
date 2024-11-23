import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from 'react-router-dom';
import MainLayout from '../layout/MainLayout';
import HomePage from '../pages/HomePage';
import ChatsPage from '../pages/ChatsPage';
import SettingsPage from '../pages/SettingsPage';
import { AuthenticationGuard } from './components/AuthenticationGuard';
import { useAuth } from '@/features/authentication/AuthContext';
import { useEffect } from 'react';
import LoginPage from '@/features/authentication/components/LoginPage';

const LogoutPage = () => {
  const { user, logout } = useAuth();

  useEffect(() => {
    if (user) logout();
  }, [user, logout]);
  return null;
};

export const routes = createRoutesFromElements(
  <Route element={<MainLayout />}>
    <Route index element={<HomePage />} />

    {/* Protected Routes */}
    <Route element={<AuthenticationGuard />}>
      <Route path="settings" element={<SettingsPage />} />
      <Route path="chats" element={<ChatsPage />} />
      <Route path="logout" element={<LogoutPage />} />
    </Route>

    {/* Login page in case unauthenticated */}
    <Route element={<AuthenticationGuard guardType="unauthenticated" />}>
      <Route path="login" element={<LoginPage />} />
    </Route>
  </Route>
);

export const router = createBrowserRouter(routes);
