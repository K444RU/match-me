/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { UserGenderEnum } from './userGenderEnum';

export interface AttributesSettingsRequestDTO {
  gender_self: UserGenderEnum;
  birth_date: string;
  /**
   * @minLength 2
   * @maxLength 100
   */
  city: string;
  /**
   * @minimum -180
   * @maximum 180
   */
  longitude?: number;
  /**
   * @minimum -90
   * @maximum 90
   */
  latitude?: number;
}
