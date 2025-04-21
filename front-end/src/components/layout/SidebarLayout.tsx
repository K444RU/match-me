import { useCommunication } from '@/features/chat';
import AppSidebar from '@/pages/chats/components/AppSidebar';
import { Outlet } from 'react-router-dom';
import { SidebarProvider } from '../ui/sidebar';

export default function SidebarLayout() {
  const { setOpenChat } = useCommunication();

  return (
    <SidebarProvider>
      <div className="flex flex-1 overflow-hidden">
        <AppSidebar onChatSelect={setOpenChat} />
        <div className="flex-grow pt-20">
          <Outlet />
        </div>
      </div>
    </SidebarProvider>
  );
}
