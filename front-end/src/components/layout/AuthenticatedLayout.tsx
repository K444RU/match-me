import { useAuth } from '@/features/authentication';
import { GlobalCommunicationProvider, WebSocketProvider } from '@/features/chat/';
import { Outlet } from 'react-router-dom';

const WS_URL = import.meta.env.VITE_WS_URL;

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
