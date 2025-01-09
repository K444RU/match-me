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
            const response = await authController.registerUser(credentials);
            console.debug('✨ AuthService: Response received');
            return response;
        } catch (error) {
            console.error('🔥 AuthService: Request failed');
            throw error;
        }
    },
};

// const login = async (email: string, password: string) => {
//   console.log('🌐 AuthService: Making login request to:', API_URL);

//   try {
//     const response = await axios.post(`${API_URL}signin`, {
//       email,
//       password,
//     });
//     console.log('✨ AuthService: Response received:', response);
//     return response;
//   } catch (error) {
//     console.error('🔥 AuthService: Request failed:', error);
//     throw error;
//   }
// };

// const register = (email: string, number: string, password: string) => {
//   return axios.post(`${API_URL}signup`, {
//     email,
//     number,
//     password,
//   });
// };

// export { login, register };
// export default { login, register };
