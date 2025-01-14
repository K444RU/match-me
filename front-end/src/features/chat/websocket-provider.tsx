import { ReactNode } from "react";
import { useWebSocketConnection } from "./use-websocket-connection";
import { WebSocketContext } from "./websocket-context";
import { StompSessionProvider } from "react-stomp-hooks";
import { MessagesSendRequestDTOWithSender } from "@/api/types";


interface WebSocketProviderProps {
  children: ReactNode;
  wsUrl: string;
  token: string;
}

export const WebSocketProvider = ({ children, wsUrl, token }: WebSocketProviderProps) => {

  const handleMessage = (message: MessagesSendRequestDTOWithSender) => {

  };

  const handleTypingIndicator = (userId: string, isTyping: boolean) => {

  };

  const handleOnlineIndicator = (userId: string, isOnline: boolean) => {

  }

  const handleConnectionChange = (connected: boolean) => {

  };

  const InnerProvider = ({ children }: { children: ReactNode }) => {
    const websocket = useWebSocketConnection({
      onMessage: handleMessage,
      onTypingIndicator: handleTypingIndicator,
      onOnlineIndicator: handleOnlineIndicator,
      onConnectionChange: handleConnectionChange,
    });

    return (
      <WebSocketContext.Provider value={websocket}>
        {children}
      </WebSocketContext.Provider>
    )
  };

  return (
    <StompSessionProvider
      url={wsUrl}
      connectHeaders={{ Authorization: `Bearer ${token}`}}
      debug={import.meta.env.DEV ? console.log : undefined}
    >
      <InnerProvider>{children}</InnerProvider>
    </StompSessionProvider>
  )
}