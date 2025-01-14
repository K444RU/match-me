import { ReactNode } from "react";
import { StompSessionProvider } from "react-stomp-hooks";
import { MessagesSendRequestDTOWithSender } from "@/api/types";
import { InnerWebSocketProvider } from "./InnerWebSocketProvider";


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

  return (
    <StompSessionProvider
      url={wsUrl}
      connectHeaders={{ Authorization: `Bearer ${token}`}}
      debug={import.meta.env.DEV ? console.log : undefined}
    >
      	<InnerWebSocketProvider
	  		onMessage={handleMessage}
			onTypingIndicator={handleTypingIndicator}
			onOnlineIndicator={handleOnlineIndicator}
			onConnectionChange={handleConnectionChange}
	  	>
			{children}
		</InnerWebSocketProvider>
    </StompSessionProvider>
  )
}