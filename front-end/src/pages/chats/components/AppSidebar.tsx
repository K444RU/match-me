import { getChatController } from '@/api/chat-controller';
import { DropdownMenu, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { useAuth } from '@/features/authentication';
import { chatService } from '@/features/chat';
import { getConnections } from '@/features/chat/connection-service';
import { cn } from '@/lib/utils';
import { ChatPreview } from '@/types/api';
import { ChevronDown } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useSubscription } from 'react-stomp-hooks';
import BlindMenu from './BlindMenu';
import ChatPreviewCard from './ChatPreviewCard';
import ConnectionsDialog from './ConnectionsDialog';
import RecommendationsDialog from './RecommendationsDialog';
import UserInfo from './UserInfo';

const AppSidebar = ({ onChatSelect }: { onChatSelect: (chat: ChatPreview) => void }) => {
  const [chats, setChats] = useState<ChatPreview[]>([]);
  const { user } = useAuth();
  const { getChatPreviews } = getChatController();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isConnectionsModalOpen, setIsConnectionsModalOpen] = useState(false);
  const [isRecommendationsModalOpen, setIsRecommendationsModalOpen] = useState(false);
  const [connections, setConnections] = useState<any>(null); // Replace 'any' with ConnectionsDTO type if available

  // Fetch chat previews
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
  }, [user, getChatPreviews]);

  // Fetch connections
  const fetchConnections = async () => {
    if (!user) return;
    try {
      const data = await getConnections(user.token);
      setConnections(data);
    } catch (error) {
      console.error('Failed to fetch connections:', error);
    }
  };

  useEffect(() => {
    fetchConnections();
  }, [user]);

  useSubscription('/user/queue/connectionUpdates', () => {
    fetchConnections();
  });

  return (
      <Sidebar>
        <SidebarContent>
          <SidebarGroup>
            <DropdownMenu open={isDropdownOpen} onOpenChange={setIsDropdownOpen}>
              <DropdownMenuTrigger asChild>
                <SidebarMenuButton
                    size="lg"
                    className={cn(
                        'justify-between group-data-[collapsible=icon]:hidden',
                        'data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground'
                    )}
                >
                  <span>Blind</span>
                  <ChevronDown className="ml-auto size-4" />
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <BlindMenu
                  setIsConnectionsModalOpen={setIsConnectionsModalOpen}
                  setIsRecommendationsModalOpen={setIsRecommendationsModalOpen}
                  setIsDropdownOpen={setIsDropdownOpen}
              />
            </DropdownMenu>
            <SidebarGroupContent>
              {chats.map((chat: ChatPreview) => (
                  <SidebarMenuItem key={chat.connectionId} className="list-none">
                    <SidebarMenuButton onClick={() => onChatSelect(chat)} className="h-fit w-full">
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
        <ConnectionsDialog
            setIsOpen={setIsConnectionsModalOpen}
            isOpen={isConnectionsModalOpen}
            connections={connections}
        />
        <RecommendationsDialog setIsOpen={setIsRecommendationsModalOpen} isOpen={isRecommendationsModalOpen} />
      </Sidebar>
  );
};

export default AppSidebar;