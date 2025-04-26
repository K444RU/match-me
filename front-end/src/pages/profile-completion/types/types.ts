import { UserGenderEnum } from '@/api/types';

export interface City {
    name: string;
    latitude: number;
    longitude: number;
    country?: string;
  }

export interface UnifiedFormData {
  firstName: string;
  lastName: string;
  alias: string | "";
  aboutMe?: string;
  hobbies?: number[] | null;
  genderSelf: UserGenderEnum;
  dateOfBirth: string;
  city: City;
  genderOther: UserGenderEnum;
  ageRange: number[];
  distance: number;
  probabilityTolerance: number;
}