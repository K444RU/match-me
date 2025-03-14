/**
 * Generated by orval v7.3.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { Hobby } from './hobby';
import type { ProfileChange } from './profileChange';
import type { User } from './user';
import type { UserAttributes } from './userAttributes';
import type { UserPreferences } from './userPreferences';

export interface UserProfile {
  alias?: string;
  attributes?: UserAttributes;
  city?: string;
  first_name?: string;
  hobbies?: Hobby[];
  id?: number;
  last_name?: string;
  preferences?: UserPreferences;
  profileChangeLog?: ProfileChange[];
  profilePicture?: string;
  user?: User;
}
