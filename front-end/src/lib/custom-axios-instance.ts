import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { STORAGE_KEYS } from '@/lib/constants/storageKeys';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

// Helper function to flatten nested objects for query parameters
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const flattenParams = (obj: any, prefix = ''): Record<string, any> => {
  return Object.keys(obj).reduce(
    (acc, key) => {
      const prefixedKey = prefix ? `${prefix}.${key}` : key;

      if (typeof obj[key] === 'object' && obj[key] !== null && !Array.isArray(obj[key])) {
        Object.assign(acc, flattenParams(obj[key], prefixedKey));
      } else {
        acc[prefixedKey] = obj[key];
      }

      return acc;
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    {} as Record<string, any>
  );
};

// Add request interceptor to include auth token in all requests
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    if (token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Custom instance that handles nested objects in query parameters
export const customInstance = async <T>(config: AxiosRequestConfig): Promise<T> => {
  // If there are params, flatten any nested objects to prevent square brackets in URLs
  if (config.params) {
    config.params = flattenParams(config.params);
  }

  // Await the actual response from axiosInstance
  const response: AxiosResponse<T> = await axiosInstance(config);
  // Return only the 'data' property, which matches the expected type T
  return response.data;
};
