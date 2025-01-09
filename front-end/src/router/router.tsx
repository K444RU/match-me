import {
    createBrowserRouter,
    createRoutesFromElements,
    Route,
} from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import HomePage from '../pages/HomePage';
import ChatsPage from '../pages/chats/ChatsPage';
import SettingsPage from '../pages/user-settings/SettingsPage';
import { AuthenticationGuard } from './components/AuthenticationGuard';
import { LoginPage, RegisterPage } from '@/features/authentication';
import ProfileCompletionPage from '@/pages/profile-completion/ProfileCompletionPage';
import AuthenticatedLayout from '@/components/layout/AuthenticatedLayout';
import PageNotFound from '@/pages/404Page';
import TestPage from '@/pages/test/TestPage';
import LogoutPage from './components/LogoutPage';

export const routes = createRoutesFromElements(
    <Route>
        {/* Public routes with main layout */}
        <Route element={<MainLayout />}>
            <Route index element={<HomePage />} />

            {/* Login page in case unauthenticated */}
            <Route
                element={<AuthenticationGuard guardType="unauthenticated" />}
            >
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
            </Route>
            <Route
                    path="*"
                    element={<PageNotFound />}
                />
        </Route>

        {/* Protected Routes */}
        {/* Auth guard before layout to prevent unnecessary rendering */}
        <Route element={<AuthenticationGuard />}>
            <Route element={<AuthenticatedLayout />}>
                <Route path="settings" element={<SettingsPage />} />
                <Route
                    path="profile-completion"
                    element={<ProfileCompletionPage />}
                />
                <Route path="chats" element={<ChatsPage />} />
                <Route path="logout" element={<LogoutPage />} />
                <Route path="test" element={<TestPage />} />
            </Route>
        </Route>
    </Route>
);

export const router = createBrowserRouter(routes, {
    future: {
        v7_fetcherPersist: true,
        v7_normalizeFormMethod: true,
        v7_partialHydration: true,
        v7_relativeSplatPath: true,
        v7_skipActionErrorRevalidation: true
      }
});
