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
import {ChatPreview} from '@/types/api';
import ChatPreviewCard from './ChatPreviewCard';
import {IFrame, StompSessionProvider} from 'react-stomp-hooks';
import {useEffect, useState} from "react";
import {useAuth} from "@features/authentication/AuthContext.tsx";
import { getMockChatPreviews } from '@/mocks/chatData';

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
            if (!user) return;
            try {
                setChats(getMockChatPreviews(user));
            } catch (error) {
                console.error('Failed to fetch chats:', error);
            }
        };

        fetchChats();
    }, [user]);

    const wsConfig = {
        url: 'http:/localhost:8000/ws',
        connectHeaders: {
            Authorization: `Bearer ${user?.token}`
        },
        debug: (str: string) => {
            console.log('WS Debug:', str);
        },
        onConnect: () => {
            console.log('WS Connected');
        },
        onDisconnect: () => {
            console.log('WS Disconnected');
        },
        onStompError: (frame: IFrame) => {
            console.error('WS Error:', frame)
        }
    }

    return (
        <Sidebar>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Blind</SidebarGroupLabel>
                    <StompSessionProvider {...wsConfig}>
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
                    </StompSessionProvider>
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
