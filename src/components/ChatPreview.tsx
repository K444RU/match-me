import React from 'react';
import { FaRegUserCircle } from 'react-icons/fa';

const ChatPreview = ({ chat }: any) => {
  console.log(chat);
  return (
    <>
      <div className="relative flex h-24 w-full items-center border-b border-background-500 bg-primary-100 text-text">
        <FaRegUserCircle className="ml-2 mr-2 h-16 w-16 text-text-600" />
        <div className="h-full w-full bg-background-300">
          <div className="mr-3 mt-3 flex justify-between">
            <p className="text-l font-bold text-text-700">{chat.name}</p>
            <p className="text-sm text-text-500">{chat.time}</p>
          </div>
          <div className="mb-3 h-fit w-full bg-background-200">
            <p className="h-full">{chat.last}</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default ChatPreview;
