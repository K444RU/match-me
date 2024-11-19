import ChatsPage from '../pages/ChatsPage';
import ChatPreview from './ChatPreview';
import UserInfo from './UserInfo'

const AllChats = () => {
  const chats: any = [
    {
      id: "1",
      name: "Andreas",
      time: "yesterday",
      last: "Hello there!"
    },
    {
      id: "2",
      name: "Karl Romet",
      time: "09:00",
      last: "What's up?"
    }
  ];
  return (
    <>
    <section className="flex flex-col items-center h-screen w-1/4 bg-background border-r border-background-500">
      <UserInfo />
      <div className="w-full overflow-scroll">
        {chats.map((chat: any) => (
          <ChatPreview key={chat.id} chat={chat} />
        ))}
        {chats.map((chat: any) => (
          <ChatPreview key={chat.id} chat={chat} />
        ))}
        {chats.map((chat: any) => (
          <ChatPreview key={chat.id} chat={chat} />
        ))}
      </div>
    </section>
    </>
  )
}

export default AllChats