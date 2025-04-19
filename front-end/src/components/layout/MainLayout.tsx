import { Toaster } from '@/components/ui/sonner';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
  return (
    <>
      <Outlet />
      <Toaster />
    </>
  );
};

export default MainLayout;
