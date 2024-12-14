import { useState } from 'react';
import OpenChat from './components/OpenChat';
import AppSidebar from './components/AppSidebar';
import { ChatPreview } from '@/types/api';

const ChatsPage = () => {
    const [openChat, setOpenChat] = useState<ChatPreview | null>(null);

    return (
        <div className="flex w-screen">
            <AppSidebar onChatSelect={setOpenChat} />
            <OpenChat chat={openChat} />
        </div>
    );
};

export default ChatsPage;
