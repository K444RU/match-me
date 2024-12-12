export interface City {
    name: string;
    latitude: number;
    longitude: number;
    country?: string;
  }

export interface UnifiedFormData {
  gender: string | null;
  dateOfBirth: string;
  city: City;
  genderOther: string | null;
  ageMin: number | null;
  ageMax: number | null;
  distance: number | null;
  probabilityTolerance: number | null;
}