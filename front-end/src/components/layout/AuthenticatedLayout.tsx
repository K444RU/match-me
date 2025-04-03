import { useAuth } from '@/features/authentication';
import { ChatProvider, WebSocketProvider } from '@/features/chat/';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';

const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8000/ws';

export default function AuthenticatedLayout() {
  const { user } = useAuth();

  if (!user) return;

  return (
    <WebSocketProvider wsUrl={WS_URL}>
      <ChatProvider>
        <SidebarProvider>
          <div className="flex h-screen w-screen overflow-hidden">
            <Outlet />
          </div>
        </SidebarProvider>
      </ChatProvider>
    </WebSocketProvider>
  );
}
