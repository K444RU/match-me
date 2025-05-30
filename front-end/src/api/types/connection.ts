/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { ConnectionResult } from './connectionResult';
import type { ConnectionState } from './connectionState';
import type { User } from './user';
import type { UserMessage } from './userMessage';

export interface Connection {
  id?: number;
  users?: User[];
  connectionStates?: ConnectionState[];
  connectionResults?: ConnectionResult[];
  userMessages?: UserMessage[];
}
