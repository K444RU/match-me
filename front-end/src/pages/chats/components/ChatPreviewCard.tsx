import type { ChatPreviewResponseDTO } from '@/api/types';
import { useWebSocket } from '@/features/chat';
import { cn } from '@/lib/utils';
import {format, fromUnixTime, isValid} from 'date-fns';
import UserAvatar from './UserAvatar';

interface ChatPreviewCardProps {
  chat: ChatPreviewResponseDTO;
  isSelected?: boolean;
}

/**
 * Formats a timestamp string into a readable 'HH:mm' format, safely handling inconsistent backend data.
 *
 * The backend sends timestamps as strings -> lastMessageTimestamp in ChatPreviewResponseDTO
 * that may represent Unix timestamps in seconds or milliseconds, or even be invalid. This function normalizes these
 * variations for consistent display in the `ChatPreviewCard`
 *
 * What errors it prevents:
 * - Crashes from null, undefined, or empty strings by returning a fallback ('--:--').
 * - "Invalid time value" errors in `date-fns` due to unvalidated numeric conversions.
 * - Incorrect times from misinterpreting seconds vs. milliseconds.
 *
 * Why it was necessary:
 * Previously, `format(fromUnixTime(chat.lastMessage.sentAt), 'kk:mm')` assumed all timestamps
 * were valid and in seconds. Backend inconsistencies (e.g., millisecond timestamps) caused runtime errors or wrong
 * time displays, breaking the chat preview.
 *
 * Why the old way failed:
 * Without validation, the app could not handle the unpredictable `lastMessageTimestamp`
 * format from the backend, leading to unreliable time rendering or crashes.
 */
function formatTimestampSafely(timestampStr: string | null | undefined): string {
  const DEFAULT_TIME = '--:--';

  // Early return for falsy inputs (null, undefined, or empty string)
  if (!timestampStr) return DEFAULT_TIME;

  const timestampNumber = Number(timestampStr);
  if (isNaN(timestampNumber)) {
    console.warn(`ChatPreviewCard: Timestamp string "${timestampStr}" could not be converted to a valid number.`);
    return DEFAULT_TIME;
  }

  try {
    // Adjust timestamp: divide by 1000 if in milliseconds
    const isMilliseconds = Math.abs(timestampNumber) > 3000000000;
    const timestampInSeconds = isMilliseconds ? timestampNumber / 1000 : timestampNumber;
    const dateObject = fromUnixTime(timestampInSeconds);

    // Validate the date
    if (!isValid(dateObject)) {
      console.warn(`ChatPreviewCard: Invalid date from timestamp: ${timestampNumber} (original: "${timestampStr}")`);
      return DEFAULT_TIME;
    }

    // Format and return the time
    return format(dateObject, 'HH:mm');
  } catch (error) {
    console.error(`ChatPreviewCard: Error processing timestamp: ${timestampNumber} (original: "${timestampStr}")`, error);
    return DEFAULT_TIME;
  }
}

export default function ChatPreviewCard({ chat, isSelected = false }: ChatPreviewCardProps) {
  const { typingUsers, onlineUsers } = useWebSocket();
  const isTyping = typingUsers[chat.connectedUserId];
  const isOnline = onlineUsers[chat.connectedUserId];

  if (!chat) return null;

  const loverName = chat.connectedUserFirstName || chat.connectedUserAlias;

  const formattedTime = formatTimestampSafely(chat.lastMessageTimestamp);

  return (
    <>
      <div className={cn('flex h-16 w-full items-center', isSelected && 'rounded-md bg-primary/40')}>
        <div className="m-2 mr-0 flex size-16 items-center justify-center">
          <div className="relative flex size-12 items-center justify-center">
            <UserAvatar name={loverName} profileSrc={chat.connectedUserProfilePicture} />
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
