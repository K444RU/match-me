import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CommunicationContext, useWebSocket } from '@/features/chat';
import { useContext, useState } from 'react';

interface OpenChatInputProps {
  onSendMessage: (message: string) => void;
  recipientAlias: string;
  isTyping: boolean;
}

export default function OpenChatInput({ onSendMessage, isTyping, recipientAlias }: OpenChatInputProps) {
  const [message, setMessage] = useState('');
  const { connected, sendTypingIndicator } = useWebSocket();
  const communicationContext = useContext(CommunicationContext);
  const openChat = communicationContext?.openChat || null;

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
    <div className="flex gap-2">
      <div className="flex w-full flex-col gap-2">
        <Input
          placeholder="Aa"
          name="message"
          type="text"
          value={message}
          onChange={handleMessageChange}
          onKeyDown={handleKeyDown}
        />
        <div className="h-4 animate-pulse text-xs">
          {isTyping ? `${recipientAlias} is typing...` : ''}
        </div>
      </div>
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
