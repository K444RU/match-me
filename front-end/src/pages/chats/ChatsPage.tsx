import OpenChat from './components/OpenChat';
import Sidebar from './components/app-sidebar'

const ChatsPage = () => {
  return (
    <div className="flex w-screen">
      <Sidebar />
      <OpenChat />
    </div>
  );
};

export default ChatsPage;
