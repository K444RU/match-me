import { createContext, useContext } from 'react';

interface WebSocketContextType {
	isConnected: boolean;
	sendConnectionRequest: (targetUserId: number) => void;
	acceptConnectionRequest: (connectionId: number) => void;
	rejectConnectionRequest: (connectionId: number) => void;
	disconnectConnection: (connectionId: number) => void;
}

export const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const useWebSocket = () => {
	const context = useContext(WebSocketContext);
	if (!context) {
		throw new Error('useWebSocket must be used within WebSocketProvider');
	}
	return context;
};
