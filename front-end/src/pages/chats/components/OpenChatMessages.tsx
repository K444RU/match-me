import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication';
import Message from './Message';

interface OpenChatMessagesProps {
  loading: boolean;
  chatMessages: ChatMessageResponseDTO[];
  user: User;
}

export default function OpenChatMessages({ loading, chatMessages, user }: OpenChatMessagesProps) {
  return (
    <div className="mt-4 size-full overflow-y-scroll">
      {loading ? (
        <div className="flex justify-center p-4">Loading messages...</div>
      ) : chatMessages.length === 0 ? (
        <div className="flex justify-center p-4">No messages yet. Start the conversation!</div>
      ) : (
        chatMessages.map((msg) => (
          <Message key={`${msg.connectionId}-${msg.messageId}`} message={msg} isOwn={msg.senderId === user.id} />
        ))
      )}
    </div>
  );
}
