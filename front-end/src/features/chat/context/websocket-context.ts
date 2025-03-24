import { MessagesSendRequestDTOWithSender } from '@/api/types';
import { createContext, useContext } from 'react';


interface WebSocketContextType {
	isConnected: boolean;
	sendMessage: (message: MessagesSendRequestDTOWithSender) => Promise<void>;
	sendTypingIndicator: (userId: string) => void;
	sendOnlineIndicator: (userId: string) => void;
}

export const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const useWebSocket = () => {
	const context = useContext(WebSocketContext);
	if (!context) {
		throw new Error('useWebSocket must be used within WebSocketProvider');
	}
	return context;
}