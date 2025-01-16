import { Chat } from "@/types/api";

interface MessageBoxProps {
    message: Chat
    isOwn?: boolean
}

export const Message = ({message, isOwn=true}: MessageBoxProps) => {
  return (
    <div className={`flex w-full ${isOwn ? 'justify-end' : ''}`}>
      <div className={`mt-2 flex max-w-[50%] flex-col ${isOwn ? ' pl-4' : 'pr-4'}`}>
        {!isOwn && (<><span>{message.sender.firstName || message.sender.alias}</span></>)}
        <div className={`size-fit rounded-md p-2 px-4 ${isOwn ? 'bg-primary-600 text-white' : 'bg-background-200'}`}>
            {message.content}
        </div>
      </div>
    </div>
  );
};