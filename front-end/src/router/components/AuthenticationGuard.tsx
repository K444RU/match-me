import { UserState } from '@/api/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { useAuth } from '@/features/authentication';
import { ReactElement } from 'react';
import { Outlet } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

export type AuthenticationGuardProps = {
  children?: ReactElement;
  redirectPath?: string;
  guardType?: 'authenticated' | 'unauthenticated';
  allowedStates?: UserState[];
};

const LOGIN_PATH = '/login';
const HOME_PATH = '/';
const CHATS_PATH = '/chats';
const PROFILE_COMPLETION_PATH = '/profile-completion';

export default function AuthenticationGuard({
  redirectPath,
  guardType = 'authenticated',
  allowedStates,
  ...props
}: AuthenticationGuardProps) {
  const { user, isLoading } = useAuth();
  let isAllowed = false;
  let finalRedirectPath = redirectPath;

  if (isLoading) {
    if (guardType === 'unauthenticated' && window.location.pathname.includes('/login')) {
      return props.children ?? <Outlet />;
    }
    return (
      <div className="flex h-[calc(100vh-80px)] w-screen items-center justify-center">
        <MotionSpinner size={32} />
      </div>
    );
  }

  if (guardType === 'unauthenticated') {
    isAllowed = !user;
    if (!isAllowed && !finalRedirectPath) {
      finalRedirectPath = user?.state === UserState.PROFILE_INCOMPLETE ? PROFILE_COMPLETION_PATH : CHATS_PATH;
    }
  } else {
    isAllowed = !!user;

    if (isAllowed && allowedStates && user?.state) {
      isAllowed = allowedStates.includes(user.state);
    }

    if (!isAllowed) {
      if (!user) {
        finalRedirectPath = finalRedirectPath ?? LOGIN_PATH;
      } else {
        if (!finalRedirectPath) {
          finalRedirectPath = user.state === UserState.PROFILE_INCOMPLETE ? PROFILE_COMPLETION_PATH : HOME_PATH;
        }
      }
    }
  }

  return <ProtectedRoute redirectPath={finalRedirectPath} isAllowed={isAllowed} {...props} />;
}
