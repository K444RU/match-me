import { createContext, ReactNode, useContext, useState } from 'react';
import { authService } from '@/features/authentication';
import { meService } from '@/features/user';
import { CurrentUserResponseDTO, LoginRequestDTO } from '@/api/types';
import { AuthenticateUserResult } from '@/api/auth-controller';

interface User extends CurrentUserResponseDTO {
    token: string;
}

interface AuthContextType {
    user: User | null;
    login: (credentials: LoginRequestDTO) => Promise<AuthenticateUserResult>;
    logout: () => void;
    fetchCurrentUser: () => Promise<void>;
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

            if (response?.data?.token) {
                localStorage.setItem('authToken', response.data.token);
                const currentUser = await meService.getCurrentUser();

                const userData: User = {
                    ...currentUser,
                    token: response.data.token,
                };

                setUser(userData);
                console.debug(
                    '✔️ AuthContext: User logged in successfully:',
                    userData
                );
                console.debug(
                    'AuthProvider user (immediately after setUser):',
                    user
                );
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

    return (
        <AuthContext.Provider value={{ user, login, logout, fetchCurrentUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextType => {
    return useContext(AuthContext);
};
