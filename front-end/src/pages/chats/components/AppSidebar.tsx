import { ChatPreviewResponseDTO } from '@/api/types';
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
import { useCommunication } from '@/features/chat';
import { cn } from '@/lib/utils';
import { ChevronDown } from 'lucide-react';
import { useState } from 'react';
import BlindMenu from './BlindMenu';
import ChatPreviewCard from './ChatPreviewCard';
import ConnectionsDialog from './ConnectionsDialog';
import RecommendationsDialog from './RecommendationsDialog';
import UserInfo from './UserInfo';
import WebSocketStatus from './WebSocketStatus';

export default function AppSidebar({ onChatSelect }: { onChatSelect: (chat: ChatPreviewResponseDTO) => void }) {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isConnectionsModalOpen, setIsConnectionsModalOpen] = useState(false);
  const [isRecommendationsModalOpen, setIsRecommendationsModalOpen] = useState(false);
  const [selectedChatId, setSelectedChatId] = useState<number | null>(null);

  const { chatPreviews, sendMarkRead } = useCommunication();

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <DropdownMenu open={isDropdownOpen} onOpenChange={setIsDropdownOpen}>
            <DropdownMenuTrigger asChild>
              <div>
                <SidebarMenuButton size="lg" className={cn('justify-between group-data-[collapsible=icon]:hidden')}>
                  <span>Blind</span>
                  <ChevronDown className="ml-auto size-4" />
                </SidebarMenuButton>
              </div>
            </DropdownMenuTrigger>
            <BlindMenu
              setIsConnectionsModalOpen={setIsConnectionsModalOpen}
              setIsRecommendationsModalOpen={setIsRecommendationsModalOpen}
              setIsDropdownOpen={setIsDropdownOpen}
            />
          </DropdownMenu>
          <SidebarGroupContent>
            {chatPreviews && chatPreviews.length > 0 ? (
              chatPreviews.map((chat: ChatPreviewResponseDTO) => (
                <SidebarMenuItem key={chat.connectionId} className="list-none">
                  <SidebarMenuButton
                    onClick={() => {
                      setSelectedChatId(chat.connectionId);
                      sendMarkRead(chat.connectionId);
                      onChatSelect(chat);
                    }}
                    className="h-fit w-full"
                  >
                    <ChatPreviewCard chat={chat} isSelected={selectedChatId === chat.connectionId} />
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))
            ) : (
              <div className="px-4 py-3 text-sm text-muted-foreground">No chats available</div>
            )}
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter>
        <SidebarMenuItem className="list-none">
          <UserInfo />
        </SidebarMenuItem>
      </SidebarFooter>
      <ConnectionsDialog setIsOpen={setIsConnectionsModalOpen} isOpen={isConnectionsModalOpen} />
      <RecommendationsDialog setIsOpen={setIsRecommendationsModalOpen} isOpen={isRecommendationsModalOpen} />
      <WebSocketStatus />
    </Sidebar>
  );
}
