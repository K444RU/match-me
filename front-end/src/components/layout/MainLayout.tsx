import { Toaster } from '@/components/ui/sonner';
import { Outlet } from 'react-router-dom';

export default function MainLayout() {
  return (
    <>
      <Outlet />
      <Toaster />
    </>
  );
};
