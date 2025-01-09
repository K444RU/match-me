import { AxiosRequestConfig } from 'axios';
import { getUserController } from '@/api/user-controller';
import {
    AccountSettingsRequestDTO,
    AttributesSettingsRequestDTO,
    PreferencesSettingsRequestDTO,
    ProfilePictureSettingsRequestDTO,
    ProfileSettingsRequestDTO,
    UserParametersRequestDTO,
} from '@/api/types';

const userController = getUserController();

export const userService = {
    updateParameters: async (
        userPararmetersRequestDTO: UserParametersRequestDTO,
        options?: AxiosRequestConfig
    ): Promise<unknown> => {
        try {
            console.debug('👤 UserService: Making updateParameters request');
            const response = await userController.setParameters(
                userPararmetersRequestDTO,
                options
            );
            return response.data;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },

    updateAccountSettings: async (
        accountSettingsRequestDTO: AccountSettingsRequestDTO
    ): Promise<unknown> => {
        try {
            console.debug('👤 UserService: Making updateAccount request');
            const token = localStorage.getItem('authToken');
            const response = await userController.updateAccount(
                accountSettingsRequestDTO,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            return response.data;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },

    updateProfileSettings: async (
        profileSettingsRequestDTO: ProfileSettingsRequestDTO
    ): Promise<unknown> => {
        try {
            console.debug('👤 UserService: Making updateProfile request');
            const token = localStorage.getItem('authToken');
            const response = await userController.updateProfile(
                profileSettingsRequestDTO,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            return response.data;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },

    updatePreferencesSettings: async (
        preferencesSettingsRequestDTO: PreferencesSettingsRequestDTO
    ): Promise<unknown> => {
        try {
            console.debug('👤 UserService: Making updatePreferences request');
            const token = localStorage.getItem('authToken');
            const response = await userController.updatePreferences(
                preferencesSettingsRequestDTO,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            return response.data;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },

    updateAttributesSettings: async (
        attributesSettingsRequestDTO: AttributesSettingsRequestDTO
    ): Promise<unknown> => {
        try {
            console.debug('👤 UserService: Making updateAttributes request');
            const token = localStorage.getItem('authToken');
            const response = await userController.updateAttributes(
                attributesSettingsRequestDTO,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            return response.data;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },

    updateProfilePicture: async (
        base64Image: ProfilePictureSettingsRequestDTO,
        options?: AxiosRequestConfig
    ): Promise<unknown> => {
        try {
            console.debug(
                '👤 UserService: Making updateProfilePicture request'
            );
            const response = await userController.uploadProfilePicture(
                base64Image,
                options
            );
            return response;
        } catch (error) {
            console.error('👤 UserService: Request failed');
            throw error;
        }
    },
};
