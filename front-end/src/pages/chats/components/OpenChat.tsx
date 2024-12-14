import { ChatPreview } from '@/types/api';
import { NoChat } from './NoChat';
import { Message } from './Message';
import InputField from '@/components/ui/forms/InputField';
import Button from '@/components/ui/buttons/Button';
import { mockChats } from '@/mocks/chatData';
import { useState } from 'react';
import sendMessage from '../ChatService';
import { useAuth } from '@/features/authentication/AuthContext';

export default function OpenChat({ chat }: { chat: ChatPreview | null }) {
    const { user } = useAuth();
    const [message, setMessage] = useState('');

    // Early return if no user
    if (!user) return null;
    if (!chat) return <NoChat />;

    const connectionChats = mockChats
        .filter((msg) => msg.connectionId === chat.connectionId)
        .sort((a, b) => a.sentAt - b.sentAt);

    const handleSendMessage = () => {
        if (!message) return;
        sendMessage(message, chat.connectionId, user.token);
        setMessage('');
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSendMessage();
        }
    };

    // TODO: Implement Chat message fetching here

    return (
        <div className="flex w-full flex-col bg-background-400 px-4 pb-4  sm:px-6 md:px-8">
            <div className="mt-4 h-full w-full overflow-y-scroll">
                {connectionChats.map((msg, index) => (
                    <Message
                        key={index}
                        message={msg}
                        isOwn={msg.sender.id === user.id}
                    />
                ))}
            </div>
            <div className="mt-4 flex gap-2">
                <InputField
                    placeholder="Aa"
                    name="message"
                    type="text"
                    value={message}
                    onChange={setMessage}
                    onKeyDown={handleKeyDown}
                />
                <Button onClick={handleSendMessage}>Send</Button>
            </div>
        </div>
    );
}
