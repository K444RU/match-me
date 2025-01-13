import { FC } from 'react';
import { SidebarProvider } from '../ui/sidebar';
import { Outlet } from 'react-router-dom';
import { ChatProvider } from '@pages/chats/ChatContext';

// Syntax recommended by Claude after asking about props - kinda cool
const AuthenticatedLayout: FC = () => {
    return (
        <ChatProvider>
            <SidebarProvider>
                <div className="flex h-screen w-screen overflow-hidden">
                    <Outlet />
                </div>
            </SidebarProvider>
        </ChatProvider>
    );
};

export default AuthenticatedLayout;
