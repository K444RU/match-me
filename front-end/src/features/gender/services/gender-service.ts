import { GenderTypeDTO } from '@/api/types';
import { getUserGenderTypeController } from '@/api/user-gender-type-controller';

const genderController = getUserGenderTypeController();

export const genderService = {
  getGenders: async (): Promise<GenderTypeDTO[]> => {
    try {
      console.debug('🖖 GenderService: Making request');
      const token = localStorage.getItem('authToken');
      const response = await genderController.getAllGenders({
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error('❌ Error fetching genders', error);
      throw error;
    }
  },
};
