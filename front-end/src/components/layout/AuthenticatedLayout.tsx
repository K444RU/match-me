import { useAuth } from '@/features/authentication';
import { GlobalCommunicationProvider } from '@/features/chat/';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';

const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8000/ws';

export default function AuthenticatedLayout() {
  const { user } = useAuth();

  if (!user) return;

  return (
    <GlobalCommunicationProvider wsUrl={WS_URL}>
      <SidebarProvider>
        <div className="flex h-screen w-screen overflow-hidden">
          <Outlet />
        </div>
      </SidebarProvider>
    </GlobalCommunicationProvider>
  );
}
