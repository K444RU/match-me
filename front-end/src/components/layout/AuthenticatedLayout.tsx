import { useAuth } from '@/features/authentication';
import { WebSocketProvider } from '@/features/chat/websocket-provider';
import { FC } from 'react';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8000/ws';

const AuthenticatedLayout: FC = () => {
  const { user } = useAuth();

  if (!user) return;

  return (
    <WebSocketProvider wsUrl={WS_URL} token={user?.token}>
      <SidebarProvider>
        <div className="flex h-screen w-screen overflow-hidden">
          <Outlet />
        </div>
      </SidebarProvider>
    </WebSocketProvider>
  );
};

export default AuthenticatedLayout;
