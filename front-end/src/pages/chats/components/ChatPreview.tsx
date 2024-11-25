import { FaRegUserCircle } from 'react-icons/fa';

const ChatPreview = ({ chat }: any) => {
  console.log(chat);
  return (
    <>
      <div className="flex h-24 w-full items-center border-b border-background-500 bg-primary-100 text-text">
        <FaRegUserCircle className="ml-2 mr-2 h-16 w-16 text-text-600" />
        <div className="flex h-full w-full flex-col bg-background-300">
          <div className="mr-3 mt-4 flex justify-between">
            <p className="font-bold leading-none text-text-700">{chat.name}</p>
            <p className="text-sm leading-none text-text-500">{chat.time}</p>
          </div>
          <div className="mb-3 mt-1 w-full flex-1 bg-background-200">
            <p className="text-sm">{chat.last}</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default ChatPreview;
