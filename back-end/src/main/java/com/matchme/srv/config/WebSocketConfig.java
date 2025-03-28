package com.matchme.srv.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

  private static final String WS_ENDPOINT = "/ws";
  private static final String CSRF_TOKEN_ATTR = "CSRF_TOKEN";
  private static final String[] ALLOWED_ORIGINS = {
      "http://localhost:8000", 
      "http://localhost:3000", 
      "http://127.0.0.1:3000"
  };
  private static final String APPLICATION_DESTINATION_PREFIX = "/app";
  private static final String USER_DESTINATION_PREFIX = "/user";
  private static final String[] SIMPLE_BROKER_DESTINATIONS = {"/topic", USER_DESTINATION_PREFIX};

  @Override
  public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
    registration.interceptors(webSocketAuthInterceptor);
  }

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
    config.enableSimpleBroker(SIMPLE_BROKER_DESTINATIONS)
            .setHeartbeatValue(new long[]{10000, 10000}) // Server sends heartbeats every 10s, expects client heartbeats every 10s
            .setTaskScheduler(taskScheduler()); // Provide the TaskScheduler
    config.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX);
    config.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry.addEndpoint(WS_ENDPOINT) //connects to the configured endpoint here
      .setAllowedOrigins(ALLOWED_ORIGINS)
      .withSockJS()
      .setSessionCookieNeeded(true);
  }

  @Bean
  public WebSocketHandlerDecoratorFactory wsHandlerDecoratorFactory() {
    return handler -> new WebSocketHandlerDecorator(handler) {
      @Override
      public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String csrfToken = UUID.randomUUID().toString();
        session.getAttributes().put(CSRF_TOKEN_ATTR, csrfToken);
        super.afterConnectionEstablished(session);
      }
    };
  }

  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1); // Single thread for scheduling heartbeats
    scheduler.setThreadNamePrefix("wss-heartbeat-thread-");
    scheduler.initialize();
    return scheduler;
  }
}
