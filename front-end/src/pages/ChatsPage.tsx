import AllChats from './AllChats';
import OpenChat from '../components/ui/OpenChat';

const ChatsPage = () => {
  return (
    <div className="flex w-screen">
      <AllChats />
      <OpenChat />
    </div>
  );
};

export default ChatsPage;
