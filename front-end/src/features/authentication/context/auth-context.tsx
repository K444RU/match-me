import { CurrentUserResponseDTO, LoginRequestDTO } from '@/api/types';
import { createContext } from 'react';

export interface AppError {
  title: string;
	subtitle: string;
	status?: number;
}

export interface LoginResult {
  success: boolean;
	user?: User;
	error?: AppError;
}

export interface User extends CurrentUserResponseDTO {
  token: string;
}
export interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (credentials: LoginRequestDTO) => Promise<LoginResult>;
  logout: () => void;
  fetchCurrentUser: () => Promise<void>;
  error: AppError | null;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  login: async () => await Promise.resolve({ success: false, error: { title: 'Not Initialized', subtitle: 'AuthContext not initialized' } }),
	logout: () => {},
  fetchCurrentUser: async () => await Promise.reject(new Error('AuthContext not initialized')),
  error: null,
});
