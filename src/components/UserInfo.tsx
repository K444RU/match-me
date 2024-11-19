import React from 'react'
import { IoSettingsOutline } from "react-icons/io5";
import { FaRegUserCircle } from "react-icons/fa";

const UserInfo = () => {
  const user: any = {
    photo: "photo",
    name: "Andreas",
    alias: "(Alias)"
  }
  return (
    <div className="relative w-full h-40 bg-primary-200 text-text flex items-center mt-20">
      <IoSettingsOutline className="absolute top-4 right-4 bg-primary-300 hover:bg-primary-400 hover:cursor-pointer text-primary-50 h-8 w-8 p-1.5 rounded-3xl" />
        <FaRegUserCircle className="text-text-600 h-20 w-20 ml-2 mr-2" />
        <div>
          <h2 className="text-2xl font-bold text-text-700 ml-2">{user.name}</h2>
          <h2 className="text-lg font-bold text-text-700 ml-2">{user.alias}</h2>
        </div>
      </div>
  )
}

export default UserInfo