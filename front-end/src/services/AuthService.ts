import axios from 'axios';

const API_URL = 'http://localhost:3000/api/auth/';

const login = (email: string, password: string) => {
  return axios.post(`${API_URL}signin`, { email, password });
};

const register = (email: string, number: string, password: string) => {
  return axios.post(`${API_URL}signup`, {
    email,
    number,
    password,
  });
};

export { login, register };
