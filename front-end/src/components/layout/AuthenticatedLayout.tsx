import { FC } from 'react';
import { SidebarProvider } from '../ui/sidebar';
import { Outlet } from 'react-router-dom';
import { WebSocketProvider } from '@/features/chat/websocket-provider';
import { useAuth } from '@/features/authentication';

const WS_URL = 'http://localhost:8000/ws';

const AuthenticatedLayout: FC = () => {
    const { user } = useAuth();

    if (!user) return;

    return (
        <WebSocketProvider
        wsUrl={WS_URL}
        token={user?.token}
        >
            <SidebarProvider>
                <div className="flex h-screen w-screen overflow-hidden">
                    <Outlet />
                </div>
            </SidebarProvider>
        </WebSocketProvider>
    );
};

export default AuthenticatedLayout;
