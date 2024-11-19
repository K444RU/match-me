import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import JumpToTopButton from '../components/JumpToTopButton';

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
