import { Toaster } from '@/components/ui/sonner';
import { Outlet } from 'react-router-dom';

export default function MainLayout() {
  return (
    <div className="absolute inset-0 h-full w-full bg-transparent bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:24px_24px]">
      <Outlet />
      <Toaster />
    </div>
  );
}
