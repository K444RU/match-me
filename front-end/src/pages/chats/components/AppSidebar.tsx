import UserInfo from './UserInfo';

import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarMenuButton,
    SidebarMenuItem,
} from '@/components/ui/sidebar';
import { useContext } from 'react';
import { ChatPreview } from '@/types/api';
import ChatPreviewCard from './ChatPreviewCard';
import { ChatContext } from '../ChatContext';

// Read on usage here: https://ui.shadcn.com/docs/components/sidebar



const AppSidebar = () => {
    const chatContext = useContext(ChatContext);
    if (!chatContext) return null;

    const { chatPreviews: chats, setOpenChat } = chatContext;

    return (
        <Sidebar>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Blind</SidebarGroupLabel>
                    {/* <AllChats /> */}
                    <SidebarGroupContent>
                        {chats?.map((chat: ChatPreview) => (
                            <SidebarMenuItem
                                key={chat.connectionId}
                                className="list-none"
                            >
                                <SidebarMenuButton
                                    onClick={() =>
                                        setOpenChat(chat.connectionId)
                                    }
                                    className="h-fit w-full"
                                >
                                    <ChatPreviewCard chat={chat} />
                                </SidebarMenuButton>
                            </SidebarMenuItem>
                        ))}
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
            <SidebarFooter>
                <SidebarMenuItem className="list-none">
                    <UserInfo />
                </SidebarMenuItem>
            </SidebarFooter>
        </Sidebar>
    );
};

export default AppSidebar;
