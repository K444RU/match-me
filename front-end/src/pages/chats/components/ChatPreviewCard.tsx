import type { ChatPreviewResponseDTO } from '@/api/types';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useWebSocket } from '@/features/chat';
import { format, fromUnixTime } from 'date-fns';

export default function ChatPreviewCard({ chat }: { chat: ChatPreviewResponseDTO }) {
  const { typingUsers, onlineUsers } = useWebSocket();
  const isTyping = typingUsers[chat.connectionId];
  const isOnline = onlineUsers[chat.connectionId];

  if (!chat) return null;

  return (
    <>
      <div className="flex h-16 w-full items-center text-text">
        <div className="m-2 flex size-16 items-center justify-center">
          <div className="relative flex size-12 items-center justify-center">
            <Avatar>
              <AvatarImage
                src={chat.connectedUserProfilePicture}
                alt={`${chat.connectedUserAlias} Avatar`}
                className="size-12 rounded-full object-cover"
              />
              <AvatarFallback>{chat.connectedUserAlias[0].toUpperCase()}</AvatarFallback>
            </Avatar>
            {isOnline ? (
              <span className="absolute bottom-0.5 right-0.5 size-4 rounded-full border-2 border-white bg-green-500"></span>
            ) : (
              <span className="absolute bottom-0.5 right-0.5 size-4 rounded-full border-2 border-white bg-gray-500"></span>
            )}
          </div>
        </div>
        <div className="flex size-full flex-col pl-2">
          <div className="mr-3 mt-4 flex justify-between">
            <p className="line-clamp-1 font-bold leading-none text-text-700">
              {chat.connectedUserFirstName
                ? chat.connectedUserFirstName + ' ' + chat.connectedUserLastName
                : chat.connectedUserAlias}
            </p>
            <p className="text-sm leading-none">{format(fromUnixTime(Number(chat.lastMessageTimestamp)), 'kk:mm')}</p>
          </div>
          <div className="mt-1 flex w-full items-center justify-between">
            {isTyping ? (
              <p className="line-clamp-1 animate-pulse text-sm text-gray-500">typing...</p>
            ) : (
              <p className={`line-clamp-1 text-sm ${chat.unreadMessageCount > 0 ? 'font-bold' : 'opacity-70'}`}>
                {chat.lastMessageContent}
              </p>
            )}
            {chat.unreadMessageCount > 0 && (
              <span className="mr-3 flex h-5 min-w-5 items-center justify-center rounded-full bg-primary px-1.5 text-xs font-medium text-white">
                {chat.unreadMessageCount}
              </span>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
