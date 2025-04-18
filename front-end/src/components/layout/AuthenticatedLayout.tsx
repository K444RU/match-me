import { useAuth } from '@/features/authentication';
import { GlobalCommunicationProvider, WebSocketProvider } from '@/features/chat/';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';

const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8000/ws';

export default function AuthenticatedLayout() {
  const { user } = useAuth();

  if (!user) return;

  return (
    <WebSocketProvider wsUrl={WS_URL}>
      <GlobalCommunicationProvider>
        <div className="flex size-full overflow-hidden">
          <Outlet />
        </div>
      </GlobalCommunicationProvider>
    </WebSocketProvider>
  );
}
