import { createContext, useContext } from 'react';
import { CurrentUserResponseDTO, LoginRequestDTO } from '@/api/types';
import { AuthenticateUserResult } from '@/api/auth-controller';

export interface User extends CurrentUserResponseDTO {
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

export const useAuth = (): AuthContextType => {
    return useContext(AuthContext);
};
