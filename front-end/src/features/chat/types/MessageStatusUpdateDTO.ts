import { MessageEventTypeEnum } from '@/api/types';

export interface MessageStatusUpdateDTO {
  messageId: number;
  connectionId: number;
  type: MessageEventTypeEnum;
  timestamp: string;
}
