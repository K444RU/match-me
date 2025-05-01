package com.matchme.srv.config;

import com.matchme.srv.security.jwt.JwtUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketGraphQlRequest;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GraphQlWebSocketAuthConfig {

  private final JwtUtils jwtUtils;
  private final ReactiveUserDetailsService reactiveUserDetailsService;

  private static final String AUTHORIZATION_KEY = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Bean
  WebSocketGraphQlInterceptor webSocketGraphQlInterceptor() {
    return new WebSocketGraphQlInterceptor() {
      @Override
      @NonNull
      public Mono<Object> handleConnectionInitialization(
          WebSocketSessionInfo sessionInfo, Map<String, Object> connectionPayload) {
        log.debug("Handling WebSocket connection initialization. Payload: {}", connectionPayload);
        String token = getTokenFromPayload(connectionPayload);

        if (token != null && jwtUtils.validateJwtToken(token)) {
          String username = jwtUtils.getUserNameFromJwtToken(token);
          log.debug("Valid JWT found for user: {}", username);

          return reactiveUserDetailsService
              .findByUsername(username)
              .flatMap(
                  userDetails -> {
                    Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    sessionInfo.getAttributes().put("SPRING_SECURITY_CONTEXT", authentication);
                    log.info(
                        "User '{}' authenticated successfully for WebSocket session.", username);
                    return Mono.empty(); // Indicate success
                  })
              .onErrorResume(
                  e -> {
                    log.warn(
                        "Authentication failed during connection initialization: {}",
                        e.getMessage());
                    return Mono.error(new RuntimeException("Authentication failed", e));
                  });
        } else {
          log.warn("No valid Authorization token found in connection payload.");
          return Mono.error(new RuntimeException("Unauthorized: Missing or invalid token"));
        }
      }

      @Override
      @NonNull
      public Mono<WebGraphQlResponse> intercept(
          @NonNull WebGraphQlRequest request, @NonNull Chain chain) {
        if (request instanceof WebSocketGraphQlRequest graphQlRequest) {
          Authentication authentication = getAuthentication(graphQlRequest.getSessionInfo());
          if (authentication != null) {
            log.debug(
                "Found Authentication ({}) in session for request URI: {}. Propagating context.",
                authentication.getName(),
                request.getUri());

            // Mono to manage setting the thread-local SecurityContext
            Mono<Authentication> authContextMono =
                Mono.deferContextual(
                    contextView ->
                        Mono.just(authentication)
                            .doOnNext(
                                auth -> {
                                  // Set thread-local context when this part of the chain executes
                                  SecurityContextHolder.getContext().setAuthentication(auth);
                                  log.info(
                                      "Set ThreadLocal SecurityContext for user {}",
                                      auth.getName());
                                })
                            // Ensure context propagates reactively too
                            .contextWrite(contextView)
                            .contextWrite(
                                ReactiveSecurityContextHolder.withAuthentication(authentication)));

            // Chain the context setting before the main chain execution
            // and ensure cleanup happens afterwards using doFinally.
            return authContextMono
                .flatMap(auth -> chain.next(request)) // Proceed with the request chain
                .doFinally(
                    signalType -> {
                      // Clear thread-local context regardless of outcome (success, error, cancel)
                      Authentication currentAuth =
                          SecurityContextHolder.getContext().getAuthentication();
                      if (currentAuth != null && currentAuth.equals(authentication)) {
                        SecurityContextHolder.clearContext();
                        log.info(
                            "Cleared ThreadLocal SecurityContext for user {} (Signal: {})",
                            authentication.getName(),
                            signalType);
                      } else {
                        log.warn(
                            "ThreadLocal SecurityContext was not the expected one or already"
                                + " cleared before doFinally for user {} (Signal: {})",
                            authentication.getName(),
                            signalType);
                      }
                    })
                // Propagate reactive context (redundant with addition inside authContextMono but
                // safe)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

          } else {
            log.debug(
                "No Authentication found in session attributes for request URI: {}",
                request.getUri());
          }
        }
        // Fallback for non-WebSocket requests or if no authentication found
        return chain.next(request);
      }
    };
  }

  // Helper to retrieve Authentication from session attributes
  private Authentication getAuthentication(WebSocketSessionInfo sessionInfo) {
    Object context = sessionInfo.getAttributes().get("SPRING_SECURITY_CONTEXT");
    if (context instanceof Authentication authentication) {
      log.debug(
          "Retrieved Authentication from WebSocket session attributes for user {}.",
          authentication.getName());
      return authentication;
    }
    log.debug("No Authentication found in WebSocket session attributes.");
    return null;
  }

  // Helper to extract token, similar to your old interceptor but for the payload Map
  private String getTokenFromPayload(Map<String, Object> payload) {
    Object authValue = payload.get(AUTHORIZATION_KEY);
    if (authValue instanceof String authToken) {
      if (authToken.startsWith(BEARER_PREFIX)) {
        return authToken.substring(BEARER_PREFIX.length());
      }
      log.warn("Authorization value does not start with Bearer prefix: {}", authToken);
      return null;
    }
    log.debug("No '{}' key found in connection payload or it's not a String.", AUTHORIZATION_KEY);
    return null;
  }
}
