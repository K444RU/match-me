package com.matchme.srv.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import com.matchme.srv.model.enums.UserState;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.security.services.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
   
    private static final List<String> PROFILE_REQUIRED_PATHS = Arrays.asList(
    		"/api/chats/**",
    		"/api/recommendations/**"
    );

    private static final Logger authTokenFilterLogger = LoggerFactory.getLogger(AuthTokenFilter.class);
  
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
  
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
  
          SecurityContextHolder.getContext().setAuthentication(authentication);

          if (authentication.getPrincipal() instanceof UserDetailsImpl userDetailsImpl) {
            Long userId = userDetailsImpl.getId();
            User user = userRepository.findById(userId).orElse(null);
         
            if (user != null && user.getState() == UserState.PROFILE_INCOMPLETE) {
              String requestUri = request.getRequestURI();
              boolean requiresCompletedProfile = PROFILE_REQUIRED_PATHS.stream()
                      .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));

              if (requiresCompletedProfile) {
                authTokenFilterLogger.warn("Access denied for user ID: {} to path: {}. Reason: Profile incomplete.", userId, requestUri);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: User profile is incomplete.");
                return;
              }
            }
          }
        }
      } catch (Exception e) {
        authTokenFilterLogger.error("Cannot set user authentication: {}", e.getMessage(), e);
      }

      filterChain.doFilter(request, response);
    }
  
    private String parseJwt(HttpServletRequest request) {
      String headerAuth = request.getHeader("Authorization");
  
      if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
        return headerAuth.substring(7, headerAuth.length());
      }
  
      return null;
    }
  }