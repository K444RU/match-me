package com.matchme.srv.config;


import java.util.List;

import org.junit.jupiter.api.Order;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import com.matchme.srv.exception.AuthenticationException;
import com.matchme.srv.security.jwt.JwtUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final JwtUtils jwtUtils;
  private final UserDetailsService userDetailsService;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      
      String authToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
      if (authToken == null || authToken.isEmpty()) {
        throw new AuthenticationException("No auth token provided");
      }

      if (authToken.startsWith(BEARER_PREFIX)) {
        authToken = authToken.substring(7);
      }

      try {
        if (!jwtUtils.validateJwtToken(authToken)) {
          throw new AuthenticationException("Invalid JWT token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(authToken);

        List<GrantedAuthority> authorities = jwtUtils.getAuthoritiesFromJwtToken(authToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        accessor.setUser(authentication);
      } catch (Exception e) {
        throw new AuthenticationException("Authentication failed:" + e.getMessage());
      }
    }
    return message;
  }
}


