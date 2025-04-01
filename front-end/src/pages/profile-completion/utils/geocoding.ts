import axios from 'axios';
import { City } from '../types/types';

const API_BASE_URL = import.meta.env.VITE_GEOAPI_BASE_URL;
const API_KEY = import.meta.env.VITE_GEOAPI_KEY;

export const geocodingService = {
  searchByCity: async (city: string): Promise<City[]> => {
    if (!API_KEY || !API_BASE_URL) {
      throw new Error('Missing GEO API key');
    }

    try {
      const response = await axios.get(API_BASE_URL, {
        params: { city, country: 'Estonia' },
        headers: { 'X-Api-Key': API_KEY },
      });
      return response.data;
    } catch (error) {
      console.error('Geocoding API error:', error);
      throw new Error('Failed to fetch city data');
    }
  },
};
