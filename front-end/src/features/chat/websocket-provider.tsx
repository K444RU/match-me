import { ReactNode } from 'react';
import { StompSessionProvider } from 'react-stomp-hooks';
import { InnerWebSocketProvider } from './InnerWebSocketProvider';

interface WebSocketProviderProps {
    children: ReactNode;
    wsUrl: string;
    token: string;
    onConnectionChange: (connected: boolean) => void;
}

export const WebSocketProvider = ({
                                      children,
                                      wsUrl,
                                      token,
                                      onConnectionChange,
                                  }: WebSocketProviderProps) => {
    return (
        <StompSessionProvider
            url={wsUrl}
            connectHeaders={{ Authorization: `Bearer ${token}` }}
            heartbeatIncoming={10000}
            heartbeatOutgoing={10000}
            debug={(msg: string) => console.log('STOMP DEBUG:', msg)}
        >
            <InnerWebSocketProvider onConnectionChange={onConnectionChange}>
                {children}
            </InnerWebSocketProvider>
        </StompSessionProvider>
    );
};
