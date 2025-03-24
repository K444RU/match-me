import { ReactNode } from "react";
import { useWebSocketConnection } from "../hooks/use-websocket-connection";
import { MessagesSendRequestDTOWithSender } from "@/api/types";
import { WebSocketContext } from "./websocket-context";

interface InnerWebSocketProviderProps {
    children: ReactNode;
    onMessage: (message: MessagesSendRequestDTOWithSender) => void;
    onTypingIndicator: (userId: string, isTyping: boolean) => void;
    onOnlineIndicator: (userId: string, isOnline: boolean) => void;
    onConnectionChange: (connected: boolean) => void;
}

export const InnerWebSocketProvider = ({
    children,
    onMessage,
    onTypingIndicator,
    onOnlineIndicator,
    onConnectionChange
}: InnerWebSocketProviderProps) => {
    const websocket = useWebSocketConnection({
      onMessage,
      onTypingIndicator,
      onOnlineIndicator,
      onConnectionChange,
    });

    return (
      <WebSocketContext.Provider value={websocket}>
        {children}
      </WebSocketContext.Provider>
    );
};
