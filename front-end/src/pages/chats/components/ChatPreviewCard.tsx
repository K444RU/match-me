import type { ChatPreviewResponseDTO } from '@/api/types';
import { useWebSocket } from '@/features/chat';
import { format, fromUnixTime } from 'date-fns';
import { FaRegUserCircle } from 'react-icons/fa';

export default function ChatPreviewCard({ chat }: { chat: ChatPreviewResponseDTO }) {
  const { typingUsers } = useWebSocket();
  const isTyping = typingUsers[chat.connectionId];

  console.log(chat);

  if (!chat || chat.connectedUserId === -1) return null;

  return (
    <>
      <div className="flex h-16 w-full items-center text-text">
        <div className="m-2 flex size-16 items-center justify-center">
          {chat.connectedUserProfilePicture ? (
            <div className="flex size-12 items-center justify-center">
              <img
                src={chat.connectedUserProfilePicture}
                alt={`${chat.connectedUserAlias} Avatar`}
                className="size-12 rounded-full object-cover"
              />
            </div>
          ) : (
            <div className="flex size-12 items-center justify-center">
              <FaRegUserCircle className="size-12 text-text-600" />
            </div>
          )}
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
          <div className="mt-1 w-full">
            {isTyping ? (
              <p className="line-clamp-1 animate-pulse text-sm text-gray-500">typing...</p>
            ) : (
              <p className="line-clamp-1 text-sm">{chat.lastMessageContent}</p>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
