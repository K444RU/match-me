import { ReactNode } from 'react';
import { StompSessionProvider } from 'react-stomp-hooks';
import { InnerWebSocketProvider } from './InnerWebSocketProvider';
import {ConnectionUpdateMessage} from "@features/chat/connectionUpdateMessage.ts";

interface WebSocketProviderProps {
    children: ReactNode;
    wsUrl: string;
    token: string;
    onConnectionChange: (connected: boolean) => void;
    onConnectionUpdate: (update: ConnectionUpdateMessage) => void;
}

export const WebSocketProvider = ({
                                      children,
                                      wsUrl,
                                      token,
                                      onConnectionChange,
                                      onConnectionUpdate,
                                  }: WebSocketProviderProps) => {
    return (
        <StompSessionProvider
            url={wsUrl}
            connectHeaders={{ Authorization: `Bearer ${token}` }}
            debug={import.meta.env.DEV ? console.log : undefined}
        >
            <InnerWebSocketProvider
                onConnectionChange={onConnectionChange}
                onConnectionUpdate={onConnectionUpdate}
            >
                {children}
            </InnerWebSocketProvider>
        </StompSessionProvider>
    );
};