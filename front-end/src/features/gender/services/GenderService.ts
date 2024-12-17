import axios from 'axios';

interface Gender {
  id: number;
  name: string;
}

export const getGenders = async (): Promise<Gender[]> => {
  try {
    const token = localStorage.getItem('authToken');
    const response = await axios.get(
      `${import.meta.env.VITE_API_URL}/genders`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching genders:', error);
    throw error;
  }
};