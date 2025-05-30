/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { UserGenderEnum } from './userGenderEnum';

export interface SettingsResponseDTO {
  email?: string;
  number?: string;
  firstName?: string;
  lastName?: string;
  alias?: string;
  aboutMe?: string;
  hobbies?: number[];
  genderSelf?: UserGenderEnum;
  birthDate?: string;
  city?: string;
  longitude?: number;
  latitude?: number;
  genderOther?: UserGenderEnum;
  ageMin?: number;
  ageMax?: number;
  distance?: number;
  probabilityTolerance?: number;
  profilePicture?: string;
}
