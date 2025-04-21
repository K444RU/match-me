import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { RefObject, useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react';
import Message from './Message';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';

interface OpenChatMessagesProps {
  loading: boolean;
  chatMessages: ChatMessageResponseDTO[];
  user: User;
  loadOlderMessages: () => void;
  hasMoreMessages: boolean;
  isLoadingMore: boolean;
  scrollContainerRef: RefObject<HTMLDivElement>;
  connectionId: number | undefined;
}

export default function OpenChatMessages({ 
  loading, 
  chatMessages, 
  user, 
  loadOlderMessages, 
  hasMoreMessages, 
  isLoadingMore, 
  scrollContainerRef,
  connectionId }: OpenChatMessagesProps) {
  const messageEndRef = useRef<HTMLDivElement>(null);
  const shouldScrollToBottomRef = useRef(true);
  const prevScrollHeightRef = useRef<number | null>(null);
  const [isInitialLoadMap, setIsInitialLoadMap] = useState<Record<number, boolean>>({});

  const scrollToBottom = useCallback((behavior : ScrollBehavior = 'auto') => {
    messageEndRef.current?.scrollIntoView({ behavior });
  }, []); 

  // reset initial load when connectionId changes
  useEffect(() => {
    if (connectionId && isInitialLoadMap[connectionId] === undefined) {
      setIsInitialLoadMap(prev => ({ ...prev, [connectionId]: true}));
      shouldScrollToBottomRef.current = true;
    }
  }, [connectionId, isInitialLoadMap]);

  // handle scrolling and trigger older messages loading
  const handleScroll = useCallback(() => {
    const container = scrollContainerRef.current;
    if (!container) return;

    // update shouldScrollToBottomRef based on current position
    const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 150;
    shouldScrollToBottomRef.current = isNearBottom;

    // load older messages when scrolled to top
    if (container.scrollTop === 0 && hasMoreMessages && !isLoadingMore && connectionId) {
      prevScrollHeightRef.current = container.scrollHeight;
      loadOlderMessages();
    }
  }, [scrollContainerRef, hasMoreMessages, isLoadingMore, loadOlderMessages, connectionId]);

  // attach scroll listener
  useEffect(() => {
    const container = scrollContainerRef.current;
    if (container) {
      container.addEventListener('scroll', handleScroll);
      handleScroll();
      return () => container.removeEventListener('scroll', handleScroll);
    }
  }, [handleScroll, scrollContainerRef]);

  // handles scroll adjustments
  useLayoutEffect(() => {
    const container = scrollContainerRef.current;
    if (!container || !connectionId) return;

    const isInitial = isInitialLoadMap[connectionId] ?? false;

    if (prevScrollHeightRef.current !== null && !isLoadingMore) {
      const heightDifference = container.scrollHeight - prevScrollHeightRef.current;
      container.scrollTop += heightDifference;
      prevScrollHeightRef.current = null;
    } else if (isInitial && chatMessages.length > 0) {
      scrollToBottom('auto');
      setIsInitialLoadMap(prev => ({ ...prev, [connectionId]: false }));
      shouldScrollToBottomRef.current = true;
    } else if (shouldScrollToBottomRef.current && !isLoadingMore) {
      scrollToBottom('smooth');
    }
  }, [chatMessages, isLoadingMore, scrollContainerRef, scrollToBottom, connectionId, isInitialLoadMap]);

  if (loading && (!chatMessages || chatMessages.length === 0)) {
    return (
      <div className="flex flex-1 flex-col space-y-4 overflow-y-auto p-4">
        {/* Loading Skeletons */}
        {[...Array(5)].map((_, i) => (
          <Skeleton key={i} className={`h-10 w-3/5 ${i % 2 === 0 ? 'self-start' : 'self-end'}`} />
        ))}
      </div>
    );
  }

  return (
    <div ref={scrollContainerRef} className="flex-1 overflow-y-auto p-4 space-y-2">
      {/* Button to load older messages, shown if not loading and more exist */}
      {hasMoreMessages && !isLoadingMore && (
         <div className="text-center">
            <Button variant="outline" size="sm" onClick={() => {
              const container = scrollContainerRef.current;
              if(container) prevScrollHeightRef.current = container.scrollHeight; // Store height before load
              loadOlderMessages();
            }}>
              Load Older Messages
            </Button>
         </div>
      )}
      {/* Loading indicator when fetching older messages */}
      {isLoadingMore && <div className="text-center text-sm text-muted-foreground">Loading older messages...</div>}
      {/* Indicator when no more older messages */}
      {!hasMoreMessages && chatMessages.length > 0 && !isLoadingMore && (
        <div className="text-center text-xs text-muted-foreground pt-2">No older messages</div>
      )}

      
      {/* Render messages */}
      {chatMessages.map((msg, index) => {
        const key = `${msg.connectionId}-${msg.messageId}`;
        const isOwn = msg.senderId === user.id;
        // Determine if this is the last message in the array
        const isLastMessage = index === chatMessages.length - 1;

        return <Message key={key} message={msg} isOwn={isOwn} isLastMessage={isLastMessage} />;
      })}
      <div ref={messageEndRef} />
    </div>
  );
}
