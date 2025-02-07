import { getChatController } from '@/api/chat-controller';
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
import { ChatPreview } from '@/types/api';
import { useAuth } from '@features/authentication/AuthContext.tsx';
import { chatService } from '@features/chat';
import { useEffect, useState } from 'react';
import { IFrame, StompSessionProvider } from 'react-stomp-hooks';
import ChatPreviewCard from './ChatPreviewCard';
import UserInfo from './UserInfo';

// Read on usage here: https://ui.shadcn.com/docs/components/sidebar

const AppSidebar = ({ onChatSelect }: { onChatSelect: (chat: ChatPreview) => void }) => {
  const [chats, setChats] = useState<ChatPreview[]>([]);
  const { user } = useAuth();
  const { getChatPreviews } = getChatController();

  useEffect(() => {
    if (!user) return;

    (async () => {
      try {
        const { data } = await getChatPreviews({
          headers: {
            Authorization: `Bearer ${user.token}`,
            'Content-Type': 'application/json',
          },
        });
        const mappedChats = data.map((dto) => chatService.mapDtoToChatPreview(dto));
        setChats(mappedChats);
      } catch (error) {
        console.error('Failed to fetch chats: ', error);
      }
    })();
  }, [user]);

  const wsConfig = {
    url: 'http:/localhost:8000/ws',
    connectHeaders: {
      Authorization: `Bearer ${user?.token}`,
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
      console.error('WS Error:', frame);
    },
  };

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Blind</SidebarGroupLabel>
          <StompSessionProvider {...wsConfig}>
            {/* <AllChats /> */}
            <SidebarGroupContent>
              {chats.map((chat: ChatPreview) => (
                <SidebarMenuItem key={chat.connectionId} className="list-none">
                  <SidebarMenuButton onClick={() => onChatSelect(chat)} className="h-fit w-full">
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
