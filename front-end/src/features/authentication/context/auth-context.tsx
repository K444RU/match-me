import { AuthenticateUserResult } from '@/api/auth-controller';
import { CurrentUserResponseDTO, LoginRequestDTO } from '@/api/types';
import { createContext } from 'react';

export interface User extends CurrentUserResponseDTO {
  token: string;
}

export interface AuthContextType {
  user: User | null;
  login: (credentials: LoginRequestDTO) => Promise<AuthenticateUserResult>;
  logout: () => void;
  fetchCurrentUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  login: async () => await Promise.reject(new Error('AuthContext not initialized')),
  logout: () => {},
  fetchCurrentUser: async () => await Promise.reject(new Error('AuthContext not initialized')),
});
