import { Calendar, Home, Inbox, Search, Settings } from 'lucide-react';
import UserInfo from './UserInfo';

import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from '@/components/ui/sidebar';
import { useEffect, useState } from 'react';
import { useAuth } from '@/features/authentication/AuthContext';
import { ChatPreview } from '@/types/api';
import { mockChatPreviews } from '@/mocks/chatData';
import ChatPreviewCard from './ChatPreviewCard';

// Read on usage here: https://ui.shadcn.com/docs/components/sidebar

const AppSidebar = ({
    onChatSelect,
}: {
    onChatSelect: (chat: ChatPreview) => void;
}) => {
    const [chats, setChats] = useState<ChatPreview[]>([]);
    const { user } = useAuth();

    useEffect(() => {
        const fetchChats = async () => {
            if (!user?.token) return;
            try {
                setChats(mockChatPreviews);
            } catch (error) {
                console.error('Failed to fetch chats:', error);
            }
        };

        fetchChats();
    }, [user?.token]);

    return (
        <Sidebar>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Blind</SidebarGroupLabel>
                    {/* <AllChats /> */}
                    <SidebarGroupContent>
                        {chats.map((chat: ChatPreview) => (
                            <SidebarMenuItem
                                key={chat.connectionId}
                                className="list-none"
                            >
                                <SidebarMenuButton
                                    onClick={() => onChatSelect(chat)}
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
