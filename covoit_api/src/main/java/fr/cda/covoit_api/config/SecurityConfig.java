package fr.cda.covoit_api.config;

import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws BusinessException {

        final String ENDPOINT_BRAND = "/api/brands/**";
        final String ADMIN = "ADMIN";
        final String API_PERSONS = "/api/persons/**";

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/register", "/login", "/health","/v3/api-docs", "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
            .requestMatchers(HttpMethod.POST, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.PUT, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, API_PERSONS).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, API_PERSONS).hasRole(ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/persons").hasRole(ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/cars").hasRole(ADMIN)
            .requestMatchers(HttpMethod.PATCH, API_PERSONS).authenticated()
            .requestMatchers(HttpMethod.GET, API_PERSONS).authenticated()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws BusinessException {
        return config.getAuthenticationManager();
    }
}