import { UserGenderEnum } from '@/api/types';

interface Genders {
  label: string;
  value: UserGenderEnum;
}

export const genders: Genders[] = [
  { label: 'Male', value: UserGenderEnum.MALE },
  { label: 'Female', value: UserGenderEnum.FEMALE },
  { label: 'Other', value: UserGenderEnum.OTHER },
];
