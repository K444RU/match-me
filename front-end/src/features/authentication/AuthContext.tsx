import { createContext, ReactNode, useContext, useState } from 'react';
import AuthService from '@/features/authentication/services/AuthService';
import { AxiosResponse } from 'axios';
import { CurrentUser } from '@/types/api';
import { getCurrentUser } from '@/features/user/services/UserService';

interface User extends CurrentUser {
    token: string;
}

interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<AxiosResponse<any, any>>;
    logout: () => void;
    fetchCurrentUser: () => Promise<void>
}

export const AuthContext = createContext<AuthContextType>({
    user: null,
    login: async () =>
        await Promise.reject(new Error('AuthContext not initialized')),
    logout: () => {},
    fetchCurrentUser: async () =>
        await Promise.reject(new Error('AuthContext not initialized')),
});

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [user, setUser] = useState<User | null>(() => {
        const token = localStorage.getItem('authToken');
        if (token) {
            try {
                getCurrentUser()
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
                localStorage.setItem('authToken', response.data.token);
                const currentUser = await getCurrentUser();

                const userData: User = {
                    ...currentUser,
                    token: response.data.token,
                };

                setUser(userData);
                console.log(
                    '✔️ AuthContext: User logged in successfully:',
                    userData
                );
                console.log(
                    'AuthProvider user (immediately after setUser):',
                    user
                );
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

          const currentUser = await getCurrentUser();
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

    return (
        <AuthContext.Provider value={{ user, login, logout, fetchCurrentUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextType => {
    return useContext(AuthContext);
};
