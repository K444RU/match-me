package com.matchme.srv.security;

import com.matchme.srv.security.jwt.AuthEntryPointJwt;
import com.matchme.srv.security.jwt.AuthTokenFilter;
import com.matchme.srv.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // This bean is used to filter requests and extract JWT tokens from the request headers.
    // It filters for JWT tokens in the Authorization header.
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // This bean is used to authenticate users using the user details service and password encoder.
    // Dao = Database Access Object.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * https://stackoverflow.com/questions/36809528/spring-boot-cors-filter-cors-preflight-channel-did-not-succeed
     *
     * Purpose:
     * This CorsFilter bean enables Cross-Origin Resource Sharing (CORS) in our Spring Boot application.
     * It allows requests from different origins (domains) to access the API while adhering to the CORS protocol.
     * It also ensures that preflight requests (OPTIONS method) are handled automatically by the server.
     *
     * How It Fixed the OPTIONS Error:
     * Browsers send preflight requests before actual requests (like GET, POST) when:
     * - The method is not simple (e.g., DELETE or PATCH).
     * - Custom headers are included. For example, in our UserController, we send an `Authorization` header:
     *   public ResponseEntity<?> getParameters(Authentication authentication) { ... }
     * - The request comes from a different origin (e.g., frontend at http://localhost:3000 and backend at http://localhost:8000).
     *
     * Without this CorsFilter, the backend wouldn't respond properly to these OPTIONS requests,
     * causing the browser to block the actual request with a CORS error.
     *
     * What This CorsFilter Does:
     * - Automatically responds to OPTIONS requests with the necessary CORS headers.
     * - Simplifies configuration by eliminating the need for extra CORS handling in Spring Security.
     * - Allows all origins, headers, and methods (useful for development but should be restricted in production).
     *
     * Example CORS Policy it applies:
     * - Origin: *
     * - Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
     * - Allowed Headers: Authorization, Content-Type, etc.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // .permitAll() allows access to the specified paths without authentication
    // .authenticated() requires authentication for all other paths
    // .anyRequest() applies rules to all other paths not specified
    // TODO: Discuss auth required
    // Genders for example don't necessarily need to be public, they need to be pulled when you login to an unverified account
    // We can do this by just removing the gender request matcher from csrf
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/complete-registration").permitAll()
                        .requestMatchers("/api/test/all").permitAll()
                        .requestMatchers("/api/genders").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws").permitAll()
                        .requestMatchers("/v3/api-docs/").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
