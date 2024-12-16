import { useState } from 'react';
import OpenChat from './components/OpenChat';
import AppSidebar from './components/AppSidebar';
import { ChatPreview } from '@/types/api';
import { Toaster } from '@/components/ui/sonner';

const ChatsPage = () => {
    const [openChat, setOpenChat] = useState<ChatPreview | null>(null);

    return (
        <>
            <div className="flex w-screen">
                <AppSidebar onChatSelect={setOpenChat} />
                <OpenChat chat={openChat} />
            </div>
            <Toaster className='bg-black text-white'/>
        </>
    );
};

export default ChatsPage;
