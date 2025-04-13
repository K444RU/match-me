import { UserState } from '@/api/types';
import AuthenticatedLayout from '@/components/layout/AuthenticatedLayout';
import NavigationBarLayout from '@/components/layout/NavigationBarLayout';
import { LoginPage, RegisterPage } from '@/features/authentication';
import PageNotFound from '@/pages/404Page';
import ProfileCompletionPage from '@/pages/profile-completion/ProfileCompletionPage';
import UserProfilePage from '@/pages/profile/UserProfilePage.tsx';
import { createBrowserRouter, createRoutesFromElements, Route } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import ChatsPage from '../pages/chats/ChatsPage';
import HomePage from '../pages/HomePage';
import PreviewPage from '../pages/PreviewPage';
import SettingsPage from '../pages/user-settings/SettingsPage';
import AuthenticationGuard from './components/AuthenticationGuard';
import LogoutPage from './components/LogoutPage';

export const routes = createRoutesFromElements(
  <Route>
    {/* Test route */}
    <Route path="preview" element={<PreviewPage />} />
    {/* --- Main Layout Wrapper --- */}
    <Route element={<MainLayout />}>
      {/* --- NavigationBarLayout Wrapper --- */}
      <Route element={<NavigationBarLayout />}>
        {/* Public Home Page */}
        <Route index element={<HomePage />} />
        {/* --- Only unauthenticated users can access these routes --- */}
        <Route element={<AuthenticationGuard guardType="unauthenticated" />}>
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
        </Route>

        {/* Authenticated users can access these routes */}
        <Route element={<AuthenticationGuard guardType="authenticated" redirectPath="/login" />}>
          {/* Group requiring ACTIVE state */}
          <Route
            element={
              <AuthenticationGuard
                allowedStates={[UserState.ACTIVE]}
              />
            }
          >
            <Route element={<AuthenticatedLayout />}>
              <Route path="settings" element={<SettingsPage />} />
              <Route path="chats" element={<ChatsPage />} />
              <Route path="me" element={<UserProfilePage />} />
              <Route path=":id/profile" element={<UserProfilePage />} />
            </Route>
          </Route>

          {/* Group requiring PROFILE_INCOMPLETE state */}
          <Route
            element={
              <AuthenticationGuard
                allowedStates={[UserState.PROFILE_INCOMPLETE]}
              />
            }
          >
            <Route element={<AuthenticatedLayout />}>
              <Route path="profile-completion" element={<ProfileCompletionPage />} />
            </Route>
          </Route>

          {/* Route accessible by ANY authenticated user */}
          {/* No `allowedStates` means any logged-in user passes */}
          <Route path="logout" element={<LogoutPage />} />
        </Route>
        {/* --- Not Found Route (within MainLayout/NavBar) --- */}
        <Route path="*" element={<PageNotFound />} />
      </Route>
      {/* End of NavigationBarLayout */}
    </Route>
    {/* End of MainLayout */}
  </Route>
);

export const router = createBrowserRouter(routes, {
  future: {
    v7_fetcherPersist: true,
    v7_normalizeFormMethod: true,
    v7_partialHydration: true,
    v7_relativeSplatPath: true,
    v7_skipActionErrorRevalidation: true,
  },
});
