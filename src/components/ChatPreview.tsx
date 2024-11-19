import React from 'react'
import { FaRegUserCircle } from "react-icons/fa";


const ChatPreview = ({chat}: any) => {
  console.log(chat);
  return (
    <>
      <div className="relative w-full h-24 bg-primary-100 text-text flex items-center border-b border-background-500">
        <FaRegUserCircle className="text-text-600 h-16 w-16 ml-2 mr-2" />
        <div className="w-full h-full bg-background-300">
          <div className="flex justify-between mt-3 mr-3">
            <p className="text-l font-bold text-text-700">{chat.name}</p>
            <p className="text-sm text-text-500">{chat.time}</p>
          </div>
          <div className="w-full h-fit bg-background-200 mb-3">
            <p className="h-full">{chat.last}</p>
          </div>
        </div>
      </div>
    </>
  )
}

export default ChatPreview