import axios from 'axios';

const API_URL = 'http://localhost:8000/api/auth/';

const login = async (email: string, password: string) => {
  console.log('🌐 AuthService: Making login request to:', API_URL);
  
  try {
    const response = await axios.post(`${API_URL}signin`, { 
      email, 
      password 
    });
    console.log('✨ AuthService: Response received:', response);
    return response;
  } catch (error) {
    console.error('🔥 AuthService: Request failed:', error);
    throw error;
  }
};

const register = (email: string, number: string, password: string) => {
  return axios.post(`${API_URL}signup`, {
    email,
    number,
    password,
  });
};

export { login, register };
export default {login, register};