import { ReactNode } from 'react';
import { useWebSocketConnection } from './use-websocket-connection';
import { WebSocketContext } from './websocket-context';

interface InnerWebSocketProviderProps {
    children: ReactNode;
    onConnectionChange: (connected: boolean) => void;
}

export const InnerWebSocketProvider = ({
                                           children,
                                           onConnectionChange,
                                       }: InnerWebSocketProviderProps) => {
    const websocket = useWebSocketConnection({ onConnectionChange });

    return (
        <WebSocketContext.Provider value={websocket}>
            {children}
        </WebSocketContext.Provider>
    );
};
