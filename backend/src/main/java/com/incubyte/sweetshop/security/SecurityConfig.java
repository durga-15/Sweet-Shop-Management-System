package com.incubyte.sweetshop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SECURITY CONFIGURATION - Sets up application security rules
 * 
 * This configuration:
 * - Encrypts passwords
 * - Validates JWT tokens
 * - Controls which endpoints need authentication
 * - Allows frontend to communicate with backend (CORS)
 * - Prevents certain types of attacks (CSRF)
 */
@Configuration  // This is a Spring configuration class
@EnableWebSecurity  // Enable Spring Security
@EnableMethodSecurity  // Allow @PreAuthorize annotations for role-based access
@RequiredArgsConstructor
public class SecurityConfig {
    // JWT filter that checks tokens on every request
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // List of allowed frontend URLs (for CORS)
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * SECURITY FILTER CHAIN - Define security rules
     * 
     * Rules:
     * - Disable CSRF (Cross-Site Request Forgery) protection
     * - Allow CORS (Cross-Origin Requests) from frontend
     * - Use stateless sessions (JWT-based, no session storage)
     * - Public endpoints (no authentication required):
     *   - /api/auth/** (login and registration)
     *   - GET /api/sweets (view all sweets)
     *   - GET /api/sweets/** (view specific sweet)
     * - All other endpoints require authentication
     * - Add JWT filter to validate tokens
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF (we use JWT instead)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Allow CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No sessions, use JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Authentication endpoints are public
                        .requestMatchers("GET", "/api/sweets").permitAll()  // Anyone can view all sweets
                        .requestMatchers("GET", "/api/sweets/**").permitAll()  // Anyone can view sweet details
                        .anyRequest().authenticated()  // All other requests need login
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Check JWT token

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

