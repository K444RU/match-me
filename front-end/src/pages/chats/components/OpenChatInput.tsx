import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { ChatContext, useWebSocket } from '@/features/chat';
import { useContext, useState } from 'react';

interface OpenChatInputProps {
  onSendMessage: (message: string) => void;
}

export default function OpenChatInput({ onSendMessage }: OpenChatInputProps) {
  const [message, setMessage] = useState('');
  const { connected, sendTypingIndicator } = useWebSocket();
  const chatContext = useContext(ChatContext);
  const openChat = chatContext?.openChat || null;

  if (!openChat) return null;

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      onSendMessage(message);
      setMessage('');
    } else if (connected && openChat && openChat.connectionId) {
      // Send typing indicator when user is typing
      sendTypingIndicator(openChat.connectionId);
    }
  };

  const handleMessageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMessage(e.target.value);

    // Only send typing indicator if already connected
    if (connected && openChat && openChat.connectionId) {
      sendTypingIndicator(openChat.connectionId);
    }
  };

  return (
    <div className="mt-4 flex gap-2">
      <Input
        placeholder="Aa"
        name="message"
        type="text"
        value={message}
        onChange={handleMessageChange}
        onKeyDown={handleKeyDown}
      />
      <Button
        onClick={() => {
          onSendMessage(message);
          setMessage('');
        }}
      >
        Send
      </Button>
    </div>
  );
}
