import { LoginRequestDTO } from '@/api/types';
import { authService } from '@/features/authentication';
import { meService } from '@/features/user';
import { ReactNode, useState } from 'react';
import { AuthContext, User } from './auth-context';

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<User | null>(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        meService
          .getCurrentUser()
          .then((currentUser) => {
            setUser({
              ...currentUser,
              token,
            });
          })
          .catch(() => {
            localStorage.removeItem('authToken');
            setUser(null);
          });

        // Parse the token and create a basic user object
        return {
          token,
          id: 0,
          email: '',
          role: [],
          firstName: '',
          lastName: '',
          alias: '',
        };
      } catch (_e) {
        localStorage.removeItem('authToken');
        return null;
      }
    }
    return null;
  });

  const login = async (credentials: LoginRequestDTO) => {
    try {
      const response = await authService.login(credentials);

      if (response?.token) {
        localStorage.setItem('authToken', response.token);
        const currentUser = await meService.getCurrentUser();

        const userData: User = {
          ...currentUser,
          token: response.token,
        };

        setUser(userData);
        console.debug('✔️ AuthContext: User logged in successfully:', userData);
        console.debug('AuthProvider user (immediately after setUser):', user);
      } else {
        console.warn('⚠️ AuthContext: No token in response');
      }
      return response;
    } catch (error) {
      console.error('💥 AuthContext: Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('profileData');
    setUser(null);
  };

  const fetchCurrentUser = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        setUser(null);
        return;
      }

      const currentUser = await meService.getCurrentUser();
      setUser({
        ...currentUser,
        token,
      });
    } catch (error) {
      console.error('❌ fetchCurrentUser error:', error);
      localStorage.removeItem('authToken');
      setUser(null);
    }
  };

  return <AuthContext.Provider value={{ user, login, logout, fetchCurrentUser }}>{children}</AuthContext.Provider>;
};
