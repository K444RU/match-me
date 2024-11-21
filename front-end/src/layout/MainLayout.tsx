import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import JumpToTopButton from '../components/ui/JumpToTopButton';

const MainLayout = () => {
  return (
    <>
      <Navbar />
      <JumpToTopButton />
      <Outlet />
    </>
  );
};

export default MainLayout;
