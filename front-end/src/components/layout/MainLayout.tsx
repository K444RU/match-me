import { Toaster } from '@/components/ui/sonner';
import { Outlet } from 'react-router-dom';
import { ThemeProvider } from '../ThemeProvider';

const MainLayout = () => {
  return (
    <ThemeProvider>
      <Outlet />
      <Toaster className="bg-black text-white" />
    </ThemeProvider>
  );
};

export default MainLayout;
