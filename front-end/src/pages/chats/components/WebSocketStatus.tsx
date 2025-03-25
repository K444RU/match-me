import { useWebSocket } from '@/features/chat';
import { cn } from '@/lib/utils';

export default function WebSocketStatus() {
  const { connected } = useWebSocket();

  return (
    <div
      className={cn(
        'fixed bottom-4 right-4 z-50 rounded-md p-1.5 text-xs shadow-md',
        'flex items-center gap-2',
        connected ? 'border border-green-300 bg-green-100' : 'border border-red-300 bg-red-100'
      )}
    >
      <div className={cn('size-2 rounded-full', connected ? 'bg-green-500' : 'bg-red-500')} />
      <span className="dark:text-black">{connected ? 'ws 👌' : 'ws ❌'}</span>
    </div>
  );
}
