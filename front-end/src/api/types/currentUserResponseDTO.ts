/**
 * Generated by orval v7.3.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { UserRoleType } from './userRoleType';

export interface CurrentUserResponseDTO {
  alias?: string;
  email?: string;
  firstName?: string;
  id?: number;
  lastName?: string;
  profileLink?: string;
  profilePicture?: string;
  role?: UserRoleType[];
}
