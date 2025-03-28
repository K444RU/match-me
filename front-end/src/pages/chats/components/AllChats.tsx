import type { ChatPreview } from '@/types/api';
import ChatPreviewCard from './ChatPreviewCard';
import { useAuth } from '@/features/authentication';
import { useEffect, useState } from 'react';
import { getMockChatPreviews } from '@/mocks/chatData';

const AllChats = ({
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

    return (
        <>
            <section className="flex h-screen flex-col items-center border-r border-background-500 bg-background">
                <div className="w-full">
                    {chats.map((chat: ChatPreview) => (
                        <div
                            key={chat.connectionId}
                            onClick={() => onChatSelect(chat)}
                            className="cursor-pointer"
                        >
                            <ChatPreviewCard chat={chat} />
                        </div>
                    ))}
                </div>
            </section>
        </>
    );
};

export default AllChats;
