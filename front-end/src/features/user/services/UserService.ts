import axios from 'axios';
import { CurrentUser, UserProfile } from '@/types/api.ts';

export const getUserParameters = async (): Promise<UserProfile> => {
    console.log('📡 Fetching user parameters via JWT');
    try {
        const token = localStorage.getItem('authToken');
        console.log('🛡️ Token used in request: ', token);
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/user/profile`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );

        const mappedData: UserProfile = response.data;
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
