import { useAuth } from '@/features/authentication';
import { GlobalCommunicationProvider, WebSocketProvider } from '@/features/chat/';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8000/ws';

export default function AuthenticatedLayout() {
  const { user } = useAuth();

  if (!user) return;

  return (
      <WebSocketProvider wsUrl={WS_URL}>
        <GlobalCommunicationProvider>
            <SidebarProvider>
                <ToastContainer
                    position="top-right"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={true}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                    theme="light"
                    style={{ marginTop: '50px' }}
                />
                <div className="flex h-screen w-screen overflow-hidden">
                    <Outlet />
                </div>
            </SidebarProvider>
        </GlobalCommunicationProvider>
      </WebSocketProvider>
  );
}
