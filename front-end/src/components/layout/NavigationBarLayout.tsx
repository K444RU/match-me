import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar';

const NavigationBarLayout = () => {
  return (
    <div className="flex h-screen flex-col">
      {/* Navbar takes its necessary height */}
      <Navbar className="h-20"/>
      {/* This div wraps the page content and grows to fill remaining space */}
      <div className="flex-1 overflow-hidden">
        {' '}
        {/* Prevents this container from scrolling */}
        <Outlet />
      </div>
    </div>
  );
};

export default NavigationBarLayout;
