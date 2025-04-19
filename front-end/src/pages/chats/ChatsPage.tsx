import { ChatPreviewResponseDTO } from '@/api/types';
import { useCommunication } from '@/features/chat';
import { useEffect, useState } from 'react';
import OpenChat from './components/OpenChat';

export default function ChatsPage() {
  const [selectedChat, _setSelectedChat] = useState<ChatPreviewResponseDTO | null>(null);
  const { setOpenChat } = useCommunication();

  useEffect(() => {
    setOpenChat(selectedChat ?? null);
  }, [selectedChat, setOpenChat]);

  return (
    <div className="flex flex-1 size-full">
      <OpenChat />
    </div>
  );
}
