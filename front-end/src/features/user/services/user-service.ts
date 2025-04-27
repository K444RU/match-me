import {
  AccountSettingsRequestDTO,
  AttributesSettingsRequestDTO, CurrentUserResponseDTO,
  PreferencesSettingsRequestDTO,
  ProfilePictureSettingsRequestDTO,
  ProfileResponseDTO,
  ProfileSettingsRequestDTO,
  UserParametersRequestDTO,
} from '@/api/types';
import { getUserController } from '@/api/user-controller';

const userController = getUserController();

export const userService = {
  updateParameters: async (userPararmetersRequestDTO: UserParametersRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updateParameters request');
      const response = await userController.setParameters(userPararmetersRequestDTO);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  updateAccountSettings: async (accountSettingsRequestDTO: AccountSettingsRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updateAccount request');
      const response = await userController.updateAccount(accountSettingsRequestDTO);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  updateProfileSettings: async (profileSettingsRequestDTO: ProfileSettingsRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updateProfile request');
      const response = await userController.updateProfile(profileSettingsRequestDTO);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  updatePreferencesSettings: async (preferencesSettingsRequestDTO: PreferencesSettingsRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updatePreferences request');
      const response = await userController.updatePreferences(preferencesSettingsRequestDTO);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  updateAttributesSettings: async (attributesSettingsRequestDTO: AttributesSettingsRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updateAttributes request');
      const response = await userController.updateAttributes(attributesSettingsRequestDTO);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  updateProfilePicture: async (base64Image: ProfilePictureSettingsRequestDTO): Promise<unknown> => {
    try {
      console.debug('👤 UserService: Making updateProfilePicture request');
      const response = await userController.uploadProfilePicture(base64Image);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  getUser: async (userId: number): Promise<CurrentUserResponseDTO> => {
    try {
      console.debug('👤 UserService: Making getUser request');
      const response = await userController.getUser(userId);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },

  getUserProfile: async (userId: number): Promise<ProfileResponseDTO> => {
    try {
      console.debug('👤 UserService: Making getUserProfile request');
      const response = await userController.getProfile(userId);
      return response;
    } catch (error) {
      console.error('👤 UserService: Request failed');
      throw error;
    }
  },
};
