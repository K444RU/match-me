/**
 * Generated by orval v7.3.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import type { Connection } from './connection';
import type { ConnectionStatus } from './connectionStatus';
import type { ConnectionType } from './connectionType';
import type { User } from './user';

export interface ConnectionState {
  connection: Connection;
  connection_type?: ConnectionType;
  id?: number;
  requesterId?: number;
  status?: ConnectionStatus;
  targetId?: number;
  timestamp: string;
  user: User;
}
