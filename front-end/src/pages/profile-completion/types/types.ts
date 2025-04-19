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
  hobbies?: number[] | null;
  gender: string;
  dateOfBirth: string;
  city: City;
  genderOther: string;
  ageRange: number[];
  distance: number;
  probabilityTolerance: number;
}