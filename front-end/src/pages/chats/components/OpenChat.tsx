import Button from '@/components/ui/buttons/Button';
import InputField from '@/components/ui/forms/InputField';
import { useAuth } from '@/features/authentication';
import { ChatContext, chatService } from '@/features/chat';
import { getMockChats } from '@/mocks/chatData';
import { useContext, useState } from 'react';
import { Message } from './Message';
import { NoChat } from './NoChat';

export default function OpenChat() {
  const { user } = useAuth();
  const [message, setMessage] = useState('');

  const chatContext = useContext(ChatContext);
  if (!chatContext) return null;
  const { openChat } = chatContext;

  // Early return if no user
  if (!user) return null;
  if (!openChat) return <NoChat />;

  const connectionChats = getMockChats(user)
    .filter((msg) => msg.connectionId === openChat)
    .sort((a, b) => a.sentAt - b.sentAt);

  const handleSendMessage = () => {
    if (!message) return;
    chatService.sendMessage(message, openChat, user.token);
    setMessage('');
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  };

  // TODO: Implement Chat message fetching here

  return (
    <div className="flex w-full flex-col bg-background-400 px-4 pb-4 sm:px-6 md:px-8">
      <div className="mt-4 size-full overflow-y-scroll">
        {connectionChats.map((msg, index) => (
          <Message key={index} message={msg} isOwn={msg.sender.id === user.id} />
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
