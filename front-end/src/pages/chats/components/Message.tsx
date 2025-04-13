import { ChatMessageResponseDTO } from '@/api/types';
import { format, fromUnixTime } from 'date-fns';
import MessageStatus from './MessageStatus';

interface MessageBoxProps {
  message: ChatMessageResponseDTO;
  isOwn?: boolean;
  isLastMessage: boolean;
}

export default function Message({ message, isOwn = true, isLastMessage }: MessageBoxProps) {
  return (
    <div className={`flex w-full ${isOwn ? 'justify-end' : ''}`}>
      <div className={`mt-2 flex max-w-[50%] flex-col ${isOwn ? ' pl-4' : 'pr-4'}`}>
        {!isOwn && (
          <>
            <span>{message.senderAlias}</span>
          </>
        )}
        <div className="flex flex-col items-end gap-1">
          <div className="flex justify-end">
            <span className="text-xs">{format(fromUnixTime(Number(message.createdAt)), 'kk:mm')}</span>
          </div>
          <div className={`size-fit rounded-md p-2 px-4 ${isOwn ? 'bg-primary-600 text-white' : 'bg-background-200'}`}>
            {message.content}
          </div>
          {isLastMessage && <MessageStatus eventType={message.event.type} isOwn={isOwn} />}
        </div>
      </div>
    </div>
  );
}
