import { AuthProvider } from '@/features/authentication';
import { RouterProvider } from 'react-router-dom';
import { router } from './router/router';
import { ThemeProvider } from './components/theme-provider';

const App = () => {
  return (
    <ThemeProvider>
    <AuthProvider>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
