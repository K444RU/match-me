import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar';

const NavigationBarLayout = () => {
  return (
    <>
      <Navbar />
      <Outlet />
    </>
  );
};

export default NavigationBarLayout;
