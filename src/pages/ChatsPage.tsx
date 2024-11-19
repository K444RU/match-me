import React from 'react'
import AllChats from '../components/AllChats'
import OpenChat from '../components/OpenChat'

const ChatsPage = () => {
  return (
    <div className="flex w-screen">
      <AllChats />
      <OpenChat />
    </div>
  )
}

export default ChatsPage