import type { ChatPreviewResponseDTO } from '@/api/types';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useWebSocket } from '@/features/chat';
import { cn } from '@/lib/utils';
import {format, fromUnixTime, isValid} from 'date-fns';

interface ChatPreviewCardProps {
  chat: ChatPreviewResponseDTO;
  isSelected?: boolean;
}

//TODO: May be find better refactored solution. this currently allows to fix the wrong time format react error on refresh
function formatTimestampSafely(timestampStr: string | null | undefined): string {
  const DEFAULT_TIME = '--:--';

  if (timestampStr === null || timestampStr === undefined || timestampStr === '') {
    return DEFAULT_TIME;
  }

  const timestampNumber = Number(timestampStr);

  if (isNaN(timestampNumber)) {
    console.warn(`ChatPreviewCard: Timestamp string "${timestampStr}" could not be converted to a valid number.`);
    return DEFAULT_TIME;
  }

  let dateObject: Date;
  try {
    if (Math.abs(timestampNumber) > 3000000000) {
      dateObject = fromUnixTime(timestampNumber / 1000);
    } else {
      dateObject = fromUnixTime(timestampNumber);
    }
  } catch (e) {
    console.error(`ChatPreviewCard: Error creating date from timestamp number: ${timestampNumber} (original: "${timestampStr}")`, e);
    return DEFAULT_TIME;
  }


  if (!isValid(dateObject)) {
    console.warn(`ChatPreviewCard: Invalid date created from timestamp number: ${timestampNumber} (original: "${timestampStr}")`);
    return DEFAULT_TIME;
  }

  try {
    return format(dateObject, 'kk:mm');
  } catch (formatError) {
    console.error(`ChatPreviewCard: Error formatting valid date object:`, dateObject, formatError);
    return DEFAULT_TIME;
  }
}

export default function ChatPreviewCard({ chat, isSelected = false }: ChatPreviewCardProps) {
  const { typingUsers, onlineUsers } = useWebSocket();
  const isTyping = typingUsers[chat.connectedUserId];
  const isOnline = onlineUsers[chat.connectedUserId];

  if (!chat) return null;

  const formattedTime = formatTimestampSafely(chat.lastMessageTimestamp);

  return (
    <>
      <div className={cn('flex h-16 w-full items-center text-text', isSelected && 'rounded-md bg-primary-100')}>
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
            <p className="text-sm leading-none">{formattedTime}</p>
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
