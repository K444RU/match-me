import { FC } from 'react';
import { SidebarProvider } from '../ui/sidebar';
import { Outlet } from 'react-router-dom';

// Syntax recommended by Claude after asking about props - kinda cool
const AuthenticatedLayout: FC = () => {
    return (
        <SidebarProvider>
            <div className="flex h-screen w-screen overflow-hidden">
                <Outlet />
            </div>
        </SidebarProvider>
    );
};

export default AuthenticatedLayout;
