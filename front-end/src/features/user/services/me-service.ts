import { CurrentUserResponseDTO, SettingsResponseDTO } from '@/api/types';
import { getMeController } from '@/api/me-controller';

const meController = getMeController();

export const meService = {
    getUserParameters: async (): Promise<SettingsResponseDTO> => {
        console.log('📡 Fetching user parameters via JWT');
        try {
            const token = localStorage.getItem('authToken');
            console.log('🛡️ Token used in request: ', token);
            const response = await meController.getParameters({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const mappedData: SettingsResponseDTO = response.data;
            console.log('✅ Received user parameters: ', mappedData);
            return mappedData;
        } catch (error) {
            console.error('❌ Error fetching user parameters', error);
            throw error;
        }
    },

    getCurrentUser: async (): Promise<CurrentUserResponseDTO> => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await meController.getCurrentUser({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data;
        } catch (error) {
            console.error('❌ Error fetching user parameters', error);
            throw error;
        }
    },
};
