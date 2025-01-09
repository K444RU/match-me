import axios, { AxiosRequestConfig } from 'axios';
import { CurrentUser, UserProfile } from '@/types/api.ts';
import { getUserController } from '@/api/user-controller';
import { ProfilePictureSettingsRequestDTO } from '@/api/types';

const userController = getUserController();

export const getUserParameters = async (): Promise<UserProfile> => {
    console.log('📡 Fetching user parameters via JWT');
    try {
        const token = localStorage.getItem('authToken');
        console.log('🛡️ Token used in request: ', token);
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/me/settings`,
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
            `${import.meta.env.VITE_API_URL}/me`,
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

export const updateSettings = async (
    settings: UserProfile,
    section: string
): Promise<UserProfile> => {
    let suffix = '';
    let payload = {};

    switch (section) {
        case 'account':
            suffix = 'account';
            payload = {
                email: settings.email,
                number: settings.number,
            };
            break;
        case 'profile':
            suffix = 'profile';
            payload = {
                first_name: settings.firstName,
                last_name: settings.lastName,
                alias: settings.alias,
            };
            break;
        case 'preferences':
            suffix = 'preferences';
            payload = {
                gender_other: settings.genderOther,
                age_min: settings.ageMin,
                age_max: settings.ageMax,
                distance: settings.distance,
                probability_tolerance: settings.probabilityTolerance,
            };
            break;
        case 'attributes':
            suffix = 'attributes';
            payload = {
                gender_self: settings.genderSelf,
                birth_date: settings.birthDate,
                city: settings.city,
                longitude: settings.longitude,
                latitude: settings.latitude,
            };
            break;
        default:
            throw new Error('Invalid section');
    }

    try {
        const token = localStorage.getItem('authToken');
        const response = await axios.put(
            `${import.meta.env.VITE_API_URL}/users/settings/${suffix}`,
            payload,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );
        return response.data;
    } catch (error) {
        console.error('❌ Error updating account settings', error);
        throw error;
    }
};

export const updateProfilePicture = async (base64Image: ProfilePictureSettingsRequestDTO, options?: AxiosRequestConfig): Promise<unknown> => {
    try {
        console.debug('👤 UserService: Making updateProfilePicture request')
        const response = await userController.uploadProfilePicture(base64Image, options)
        return response;
    } catch (error) {
        console.error('👤 UserService: Request failed');
        throw error;
    }
}