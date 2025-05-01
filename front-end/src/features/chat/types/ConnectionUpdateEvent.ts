import { ConnectionProvider } from '@/api/types';
import { ConnectionUpdateType } from './ConnectionUpdateType';

export interface ConnectionUpdateEvent {
  action: ConnectionUpdateType;
  connection: ConnectionProvider;
}
