import { useState } from 'react';
import AllChats from './components/AllChats';
import OpenChat from './components/OpenChat';
import Sidebar from './components/app-sidebar'
import { Chat, ChatPreview } from '@/types/api';

const ChatsPage = () => {
  const [openChat, setOpenChat] = useState<ChatPreview | null>(null);
  return (
    <div className="flex w-screen">
      <Sidebar /* onChatSelect={setOpenChat} *//>
      <OpenChat chat={openChat}/>
    </div>
  );
};

export default ChatsPage;
