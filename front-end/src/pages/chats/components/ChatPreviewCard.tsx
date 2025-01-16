import { format, fromUnixTime } from 'date-fns';
import { FaRegUserCircle } from 'react-icons/fa';
import type { ChatPreview } from '@/types/api';

const ChatPreviewCard = ({ chat }: { chat: ChatPreview }) => {
    return (
        <>
            <div className="flex h-16 w-full items-center text-text">
                <div className="m-2 flex size-16 items-center justify-center">
                    {chat.participant.avatar ? (
                        <div className="flex size-12 items-center justify-center">
                            <img
                                src={chat.participant.avatar}
                                alt={`${chat.participant.alias} Avatar`}
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
