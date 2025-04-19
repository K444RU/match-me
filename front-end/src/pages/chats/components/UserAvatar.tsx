import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { cn } from '@/lib/utils';

function getInitials(name: string) {
  return name.charAt(0).toUpperCase();
}

interface UserAvatarProps {
  name: string;
  profileSrc?: string;
  avatarClassName?: string;
  imageClassName?: string;
  fallbackClassName?: string;
}

export default function UserAvatar({
  name,
  profileSrc,
  avatarClassName,
  imageClassName,
  fallbackClassName,
}: UserAvatarProps) {
  return (
    <Avatar className={cn('size-8', avatarClassName)}>
      <AvatarImage src={profileSrc} alt={`${name} profile picture`} className={imageClassName} />
      <AvatarFallback className={cn('text-muted-foreground font-semibold', fallbackClassName)}>{getInitials(name)}</AvatarFallback>
    </Avatar>
  );
}
