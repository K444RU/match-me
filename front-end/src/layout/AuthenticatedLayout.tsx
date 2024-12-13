import { FC, PropsWithChildren } from 'react'
import { SidebarProvider, SidebarTrigger } from "../components/ui/sidebar";
import AppSidebar from "../pages/chats/components/app-sidebar";

// Syntax recommended by Claude after asking about props - kinda cool
const AuthenticatedLayout: FC<PropsWithChildren> = ({ children }) => {
  return (
    <SidebarProvider>
      <AppSidebar />
      <main>
        <SidebarTrigger />
        {children}
      </main>
    </SidebarProvider>
  )
}

export default AuthenticatedLayout;