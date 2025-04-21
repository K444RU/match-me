import { MessageEventTypeEnum } from '@/api/types';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { CheckCircle2, Circle } from 'lucide-react';

interface MessageStatusProps {
  eventType: MessageEventTypeEnum;
  isOwn: boolean;
  recipientAvatar?: string;
}

const iconSize = 16;
const iconColor = 'text-secondary';

export default function MessageStatus({ eventType, isOwn, recipientAvatar = '' }: MessageStatusProps) {
  if (!isOwn) {
    // No status indicator needed for messages received from others
    return null;
  }

  let statusIcon = null;

  switch (eventType) {
    case MessageEventTypeEnum.SENT:
      // Outlined circle for sent
      statusIcon = <Circle size={iconSize} className={iconColor} />;
      break;
    case MessageEventTypeEnum.RECEIVED:
      // Filled circle for delivered/received
      statusIcon = <CheckCircle2 size={iconSize} className={iconColor} />;
      break;
    case MessageEventTypeEnum.READ:
      // Recipient's avatar for read.
      // Using a checkmark circle as a fallback.

      statusIcon = (
        <Avatar className="size-4">
          <AvatarImage src={recipientAvatar} alt={`Recipient avatar`} />
          <AvatarFallback className="bg-transparent">
            <CheckCircle2 size={iconSize} fill="currentColor" className="text-secondary/50 dark:text-secondary/75"  />
          </AvatarFallback>
        </Avatar>
      );
      break;
    default:
      return null;
  }

  return <div className="">{statusIcon}</div>;
}
