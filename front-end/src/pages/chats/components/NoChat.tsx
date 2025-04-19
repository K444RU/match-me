import { cn } from '@/lib/utils';

interface NoChatProps {
  className?: string;
}

export default function NoChat({ className }: NoChatProps) {
  return (
    <div className={cn('bg-background/40 flex size-full items-center justify-center', className)}>
      <div className="bg-background/20 rounded-full p-2 px-4">Click on a Chat to get started.</div>
    </div>
  );
}
