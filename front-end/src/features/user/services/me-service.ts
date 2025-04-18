import { getMeController } from '@/api/me-controller';
import { CurrentUserResponseDTO, ProfileResponseDTO, SettingsResponseDTO } from '@/api/types';

const meController = getMeController();

export const meService = {
  getUserParameters: async (): Promise<SettingsResponseDTO> => {
    try {
      const response = await meController.getParameters();
      return response;
    } catch (error) {
      console.error('❌ Error fetching user parameters', error);
      throw error;
    }
  },

  getCurrentUser: async (): Promise<CurrentUserResponseDTO> => {
    try {
      const response = await meController.getCurrentUser();
      return response;
    } catch (error) {
      console.error('❌ Error fetching user parameters', error);
      throw error;
    }
  },

  getUserProfile: async (): Promise<ProfileResponseDTO> => {
    try {
      const response = await meController.getCurrentProfile();
      return response;
    } catch (error) {
      console.error('❌ Error fetching user profile', error);
      throw error;
    }
  },
};
