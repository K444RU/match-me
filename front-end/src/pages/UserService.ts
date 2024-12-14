import axios from "axios";
import {userProfile} from "@/types/api.ts";

const API_URL = 'http://localhost:8000/api/user/settings/setup';

export const getUserParameters = async (): Promise<userProfile> => {
    console.log('📡 Fetching user parameters via JWT');
    try {
        const token = localStorage.getItem('authToken');
        console.log('🛡️ Token used in request: ', token);
        const response = await axios.get(API_URL, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const data = response.data;
        const mappedData: userProfile = {
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
