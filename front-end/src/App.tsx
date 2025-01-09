import { RouterProvider } from 'react-router-dom';
import { router } from './router/router';
import { AuthProvider } from './features/authentication/AuthContext';
import { StrictMode } from 'react';

const App = () => {
  return (
    <StrictMode>
      <AuthProvider>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AuthProvider>
    </StrictMode>
  );
};

export default App;
