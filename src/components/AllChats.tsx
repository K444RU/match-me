import React from 'react'
import ChatPreview from './ChatPreview';

const AllChats = () => {
  return (
    <>
    <div className="w">
      <ChatPreview />
      <ChatPreview />
      <ChatPreview /> 
      // loop user chat database...
    </div>
    </>
  )
}

export default AllChats