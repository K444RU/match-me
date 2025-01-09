import { getGenderController } from '@/api/gender-controller';
import { GenderTypeDTO } from '@/api/types';

const genderController = getGenderController();

export const genderService = {
  getGenders: async (): Promise<GenderTypeDTO[]> => {
    try {
      console.debug('🖖 GenderService: Making request')
      const token = localStorage.getItem('authToken');
      const response = await genderController.getAllGenders(
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error('❌ Error fetching genders', error);
      throw error;
    }
  }
}