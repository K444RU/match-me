import { format, fromUnixTime } from 'date-fns';
import { FaRegUserCircle } from 'react-icons/fa';
import type { ChatPreview } from '@/types/api';

const ChatPreviewCard = ({ chat }: { chat: ChatPreview }) => {
    return (
        <>
            <div className="flex h-16 w-full items-center border-b border-background-500 bg-background-300 text-text">
                <div className="m-2 flex h-16 w-16 items-center justify-center">
                    {chat.participant.avatar ? (
                        <div className="flex h-12 w-12 items-center justify-center">
                            <img
                                src={chat.participant.avatar}
                                alt={`${chat.participant.alias} Avatar`}
                                className="h-12 w-12 rounded-full object-cover"
                            />
                        </div>
                    ) : (
                        <div className="flex h-12 w-12 items-center justify-center">
                            <FaRegUserCircle className="h-12 w-12 text-text-600" />
                        </div>
                    )}
                </div>
                <div className="flex h-full w-full flex-col pl-2">
                    <div className="mr-3 mt-4 flex justify-between">
                        <p className="font-bold leading-none text-text-700 line-clamp-1">
                            {chat.participant.firstName
                                ? chat.participant.firstName +
                                  ' ' +
                                  chat.participant.lastName
                                : chat.participant.alias}
                        </p>
                        <p className="text-sm leading-none">
                            {format(
                                fromUnixTime(chat.lastMessage.sentAt),
                                'kk:mm'
                            )}
                        </p>
                    </div>
                    <div className="mt-1 w-full">
                        <p className="line-clamp-1 text-sm">
                            {chat.lastMessage.content}
                        </p>
                    </div>
                </div>
            </div>
        </>
    );
};

export default ChatPreviewCard;
