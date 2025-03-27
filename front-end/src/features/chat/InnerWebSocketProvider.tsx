import { ReactNode } from 'react';
import { useWebSocketConnection } from './use-websocket-connection';
import { WebSocketContext } from './websocket-context';
import {ConnectionUpdateMessage} from "@features/chat/connectionUpdateMessage.ts";

interface InnerWebSocketProviderProps {
    children: ReactNode;
    onConnectionChange: (connected: boolean) => void;
    onConnectionUpdate: (update: ConnectionUpdateMessage) => void;
}

export const InnerWebSocketProvider = ({
                                           children,
                                           onConnectionChange,
                                           onConnectionUpdate,
                                       }: InnerWebSocketProviderProps) => {
    const websocket = useWebSocketConnection({
        onConnectionChange,
        onConnectionUpdate,
    });

    return (
        <WebSocketContext.Provider value={websocket}>
            {children}
        </WebSocketContext.Provider>
    );
};