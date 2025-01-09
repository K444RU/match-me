import { useEffect, useState } from 'react';
import OpenChat from './components/OpenChat';
import AppSidebar from './components/AppSidebar';
import { ChatPreview } from '@/types/api';
import { Toaster } from '@/components/ui/sonner';
import { useAuth } from '@/features/authentication';
import { getMockChatPreviews } from '@/mocks/chatData';
import { ChatContext } from './ChatContext';

const ChatsPage = () => {
    const [chats, setChats] = useState<ChatPreview[]>([]);
    const [openChat, setOpenChat] = useState<number | null>(null);
    const { user } = useAuth();

    const refreshChats = async () => {
        if (!user?.token) return;
        try {
            setChats(getMockChatPreviews(user));
        } catch (error) {
            console.error('Failed to refresh chats: ', error);
        }
    };

    useEffect(() => {
        refreshChats();
    }, [user?.token]);

    return (
        <ChatContext.Provider
            value={{ chatPreviews: chats, openChat, refreshChats, setOpenChat }}
        >
            <div className="flex w-screen">
                <AppSidebar />
                <OpenChat />
            </div>
            <Toaster className="bg-black text-white" />
        </ChatContext.Provider>
    );
};

export default ChatsPage;
