import { ChatPreviewResponseDTO } from '@/api/types';
import { useCommunication } from '@/features/chat';
import { useEffect, useState } from 'react';
import AppSidebar from './components/AppSidebar';
import OpenChat from './components/OpenChat';

export default function ChatsPage() {
  const [selectedChat, setSelectedChat] = useState<ChatPreviewResponseDTO | null>(null);
  const { setOpenChat } = useCommunication();

  useEffect(() => {
    setOpenChat(selectedChat ?? null);
  }, [selectedChat, setOpenChat]);

  return (
    <>
      <div className="flex w-screen">
        <AppSidebar onChatSelect={setSelectedChat} />
        <OpenChat />
      </div>
    </>
  );
}
