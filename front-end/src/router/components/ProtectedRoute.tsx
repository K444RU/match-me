import { ReactElement } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

export type ProtectedRouteProps = { children?: ReactElement } & {
  isAllowed: boolean;
  redirectPath?: string;
};

export default function ProtectedRoute({ isAllowed, children, redirectPath = '/' }: ProtectedRouteProps) {
  const location = useLocation();

  if (!isAllowed) {
    return <Navigate to={redirectPath} state={{ from: location }} replace />;
  }

  return children ?? <Outlet />;
}
