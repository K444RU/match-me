import { AuthProvider } from '@/features/authentication';
import { RouterProvider } from 'react-router-dom';
import { router } from './router/router';

const App = () => {
  return (
    <AuthProvider>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AuthProvider>
  );
};

export default App;
