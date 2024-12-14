import axios from 'axios';
import { CurrentUser, UserProfile } from '@/types/api.ts';

export const getUserParameters = async (): Promise<UserProfile> => {
    console.log('📡 Fetching user parameters via JWT');
    try {
        const token = localStorage.getItem('authToken');
        console.log('🛡️ Token used in request: ', token);
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/user/settings/setup`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );

        const data = response.data;
        const mappedData: UserProfile = {
            firstName: data.first_name,
            lastName: data.last_name,
            alias: data.alias,
            email: data.email,
            city: data.city || 'Unknown',
            latitude: data.latitude,
            longitude: data.longitude,
        };

        console.log('✅ Received user parameters: ', mappedData);
        return mappedData;
    } catch (error) {
        console.error('❌ Error fetching user parameters', error);
        throw error;
    }
};

export const getCurrentUser = async (): Promise<CurrentUser> => {
    try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/user/currentUser`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );
        return response.data;
    } catch (error) {
        console.error('❌ Error fetching user parameters', error);
        throw error;
    }
};
