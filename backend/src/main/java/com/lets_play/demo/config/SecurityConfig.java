package com.lets_play.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets_play.demo.exception.ErrorResponse;
import com.lets_play.demo.security.jwt.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .redirectToHttps(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").authenticated()
                 
                        .requestMatchers( "/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                    .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            String message = request.getRequestURI().startsWith("/api/v1/users")
                                ? "Only administrators can access user management endpoints"
                                : "You do not have permission to access this resource";
                            ErrorResponse errorResponse = new ErrorResponse(
                                HttpServletResponse.SC_FORBIDDEN,
                                message,
                                null,
                                request.getRequestURI());
                            objectMapper.writeValue(response.getOutputStream(), errorResponse);
                        }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}