import { RouterProvider } from 'react-router-dom';
import { router } from './router/router';
import { AuthProvider } from './features/authentication/AuthContext';
import { StrictMode } from 'react';

const App = () => {
  return (
    <StrictMode>
      <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
    </StrictMode>
  );
};

export default App;
