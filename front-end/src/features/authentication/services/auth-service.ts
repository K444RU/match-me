import { AuthenticateUserResult, getAuthController, RegisterUserResult } from '@/api/auth-controller';
import { LoginRequestDTO, SignupRequestDTO } from '@/api/types';

const authController = getAuthController();

export const authService = {
  login: async (credentials: LoginRequestDTO): Promise<AuthenticateUserResult> => {
    try {
      console.debug('🌐 AuthService: Making login request');
      const response = await authController.authenticateUser(credentials);
      console.debug('✨ AuthService: Response received');
      return response;
    } catch (error) {
      console.error('🔥 AuthService: Request failed');
      throw error;
    }
  },
  register: async (credentials: SignupRequestDTO): Promise<RegisterUserResult> => {
    try {
      console.debug('🌐 AuthService: Making sign up request');
      await authController.registerUser(credentials);
    } catch (error) {
      console.error('🔥 AuthService: Request failed');
      throw error;
    }
  },
};
