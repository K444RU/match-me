import { createContext, ReactNode, useContext, useState } from 'react';
import AuthService from '@services/AuthService';
import { AxiosResponse } from 'axios';

interface User {
  token: string;
  type: string;
  id: number;
  email: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<AxiosResponse<any, any>>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  login: async () =>
    await Promise.reject(new Error('AuthContext not initialized')),
  logout: () => {},
});

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<User | null>(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        // Parse the token and create a basic user object
        return {
          token: JSON.parse(token),
          type: '',
          id: 0,
          email: '',
          role: '',
        };
      } catch (e) {
        localStorage.removeItem('authToken');
        return null;
      }
    }
    return null;
  });

  const login = async (email: string, password: string) => {
    console.log('🔑 AuthContext: Login attempt with:', { email, password });
    
    try {
      console.log('📡 AuthContext: Calling AuthService.login');
      const response = await AuthService.login(email, password);
      console.log('📥 AuthContext: Received response:', response);
      
      if (response?.data?.token) {
        console.log('🎫 AuthContext: Token found, setting user');
        localStorage.setItem('authToken', JSON.stringify(response.data.token));
        setUser(response.data);
      } else {
        console.warn('⚠️ AuthContext: No token in response');
      }
      return response;
    } catch (error: any) {
      console.error('💥 AuthContext: Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  return useContext(AuthContext);
};
