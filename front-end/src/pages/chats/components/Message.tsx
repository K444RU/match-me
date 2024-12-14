import { Chat } from "@/types/api";

interface MessageBoxProps {
    message: Chat
    isOwn?: boolean
}

export const Message = ({message, isOwn=true}: MessageBoxProps) => {
  return (
    <div className={`w-full flex ${isOwn ? 'justify-end' : ''}`}>
      <div className={`flex flex-col max-w-[50%] mt-2 ${isOwn ? ' pl-4' : 'pr-4'}`}>
        {!isOwn && (<><span>{message.sender.firstName || message.sender.alias}</span></>)}
        <div className={`w-fit h-fit p-2 px-4 rounded-md ${isOwn ? 'bg-primary-600 text-white' : 'bg-background-200'}`}>
            {message.content}
        </div>
      </div>
    </div>
  );
};