import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { useEffect, useRef } from 'react';
import Message from './Message';

interface OpenChatMessagesProps {
  loading: boolean;
  chatMessages: ChatMessageResponseDTO[];
  user: User;
}

export default function OpenChatMessages({ loading, chatMessages, user }: OpenChatMessagesProps) {
  const messageEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [chatMessages]);

  return (
    <div className="mt-4 flex-1 overflow-y-auto">
      {loading ? (
        <div className="flex justify-center p-4">Loading messages...</div>
      ) : chatMessages.length === 0 ? (
        <div className="flex justify-center p-4">No messages yet. Start the conversation!</div>
      ) : (
        chatMessages.map((msg, index) => {
          const key = `${msg.connectionId}-${msg.messageId}`;
          const isOwn = msg.senderId === user.id;
          // Determine if this is the last message in the array
          const isLastMessage = index === chatMessages.length - 1;

          return <Message key={key} message={msg} isOwn={isOwn} isLastMessage={isLastMessage} />;
        })
      )}
      <div ref={messageEndRef} />
    </div>
  );
}
