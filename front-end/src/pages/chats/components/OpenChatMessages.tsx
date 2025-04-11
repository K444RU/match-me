import { ChatMessageResponseDTO } from '@/api/types';
import { User } from '@/features/authentication';
import { RefObject, useEffect, useLayoutEffect, useRef } from 'react';
import Message from './Message';
import { Loader2 } from 'lucide-react';

interface OpenChatMessagesProps {
  loading: boolean;
  chatMessages: ChatMessageResponseDTO[];
  user: User;
  loadOlderMessages: () => void;
  hasMoreMessages: boolean;
  isLoadingMore: boolean;
  scrollContainerRef: RefObject<HTMLDivElement>;
}

export default function OpenChatMessages({ 
  loading, 
  chatMessages, 
  user, 
  loadOlderMessages, 
  hasMoreMessages, 
  isLoadingMore, 
  scrollContainerRef }: OpenChatMessagesProps) {
  const messageEndRef = useRef<HTMLDivElement>(null);
  const topSentinelRef = useRef<HTMLDivElement>(null);
  const prevScrollHeightRef = useRef<number | null>(null);

  const scrollToBottom = () => {
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // Scroll position handling
  useLayoutEffect(() => {
    const scrollContainer = scrollContainerRef.current;
    if (!scrollContainer) return;

    if (prevScrollHeightRef.current !== null) {
      const scrollHeightDiff = scrollContainer.scrollHeight - prevScrollHeightRef.current;
      scrollContainer.scrollTop = scrollHeightDiff;
      prevScrollHeightRef.current = null;
    }
  }, [chatMessages, scrollContainerRef]);

  const pageSize = 10;

  // intersection observer for top sentinel
  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      const firstEntry = entries[0];
      console.log(
        `Observer Callback: isIntersecting=${firstEntry.isIntersecting}, hasMoreOlderMessages=${hasMoreMessages}, isLoadingMore=${isLoadingMore}`
      );
      if (firstEntry.isIntersecting && hasMoreMessages && !isLoadingMore && chatMessages.length >= pageSize) {
        const scrollContainer = scrollContainerRef.current;
        if (scrollContainer) {
          prevScrollHeightRef.current = scrollContainer.scrollHeight;
        }
        loadOlderMessages();
      }
      },
      {
        root: scrollContainerRef.current,
        threshold: 1.0,
      }
    );

    const currentTopSentinel = topSentinelRef.current;
    if (currentTopSentinel) {
      observer.observe(currentTopSentinel);
    }

    return () => {
      if (currentTopSentinel) {
        observer.unobserve(currentTopSentinel);
      }
    };
  }, [hasMoreMessages, isLoadingMore, loadOlderMessages, scrollContainerRef]);

  // Scroll to bottom logic
  // useEffect(() => {
  //   const scrollContainer = scrollContainerRef.current;
  //   if (scrollContainer && !isLoadingMore) {
  //     const isNearBottom = scrollContainer.scrollHeight - scrollContainer.scrollTop - scrollContainer.clientHeight < 100;
  //     if (isNearBottom) {
  //       messageEndRef.current?.scrollIntoView({ behavior: "auto" })
  //     }
  //   }
  // }, [chatMessages.length, isLoadingMore, scrollContainerRef, loading]);

  useEffect(() => {
    scrollToBottom();
  }, [chatMessages]);

  return (
    <div ref={scrollContainerRef} className="mt-4 flex-1 overflow-y-scroll pr-4">
      {/* Older messages loading */}
      <div ref={topSentinelRef} style={{ height: '1px'}} />
      {isLoadingMore && (
        <div className="flex items-center justify-center p-2 text-sm text-muted-foreground">
          <Loader2 className="mr-2 size-4 animate-spin" />
          Loading older messages...
        </div>
      )}
      {/* Initial loading */}
      {loading && !isLoadingMore ? (
        <div className="flex h-full items-center justify-center p-4">Loading messages...</div>
      ) : chatMessages.length === 0 && !loading ? (
        <div className="flex h-full items-center justify-center p-4">No messages yet. Start the conversation!</div>
      ) : (
        chatMessages.map((msg) => (
          <Message key={`${msg.connectionId}-${msg.messageId}`} message={msg} isOwn={msg.senderId === user.id} />
        ))
      )}
      <div ref={messageEndRef} />
    </div>
  );
}
