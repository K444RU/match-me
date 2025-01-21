package com.matchme.srv.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketAuthInterceptor webSocketAuthInterceptor;

  @Override
  public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
    registration.interceptors(webSocketAuthInterceptor);
  }
  
  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/user"); // the client subscribes
    config.setApplicationDestinationPrefixes("/app"); //These go to server
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry.addEndpoint("/ws") //connects to the configured endpoint here
      .setAllowedOrigins("http://localhost:8000", "http://localhost:3000", "http://127.0.0.1:3000")
      .withSockJS()
      .setSessionCookieNeeded(true);
  }

  @Bean
  public WebSocketHandlerDecoratorFactory wsHandlerDecoratorFactory() {
    return handler -> new WebSocketHandlerDecorator(handler) {
      @Override
      public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String csrfToken = UUID.randomUUID().toString();
        session.getAttributes().put("CSRF_TOKEN", csrfToken);
        super.afterConnectionEstablished(session);
      }
    };
  }
}
