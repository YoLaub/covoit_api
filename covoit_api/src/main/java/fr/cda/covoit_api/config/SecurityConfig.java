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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Classe de configuration maîtresse pour Spring Security.
 * Définit la politique de sécurité, le hachage des mots de passe,
 * et les règles d'accès basées sur les rôles (RBAC).
 *
 * @author Yoann Laubert
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit l'algorithme de hachage pour les mots de passe.
     * Utilise BCrypt, un algorithme adaptatif robuste contre les attaques par force brute.
     *
     * @return Instance de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de la chaîne de filtres de sécurité.
     *
     * Points clés :
     * 1. Désactivation du CSRF (inutile pour une API stateless JWT).
     * 2. Politique de session STATELESS (aucune session côté serveur).
     * 3. Définition des accès :
     *    - Publique : /login, /register, documentation Swagger.
     *    - Restricted (ADMIN) : Gestion des marques et suppression de comptes.
     *    - Authenticated : Accès général aux trajets et profils.
     *
     * @param http L'objet de configuration HTTP Security.
     * @param jwtFilter Le filtre personnalisé pour intercepter et valider les tokens JWT.
     * @return La chaîne de filtres configurée.
     * @throws BusinessException en cas de configuration invalide.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws BusinessException {

        final String ENDPOINT_BRAND = "/api/brands/**";
        final String ADMIN = "ADMIN";
        final String API_PERSONS = "/api/persons/**";
        final String API_MODEL = "/api/models/**";

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            // Routes publiques
            .requestMatchers("/register", "/login", "/forgot-password", "/reset-password", "/health","/v3/api-docs", "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
            // Accès restreints aux Administrateurs
            .requestMatchers(HttpMethod.POST, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.PUT, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, ENDPOINT_BRAND).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, API_PERSONS).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, API_PERSONS).hasRole(ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/persons").hasRole(ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/cars").hasRole(ADMIN)
            .requestMatchers(HttpMethod.POST, API_MODEL).hasRole(ADMIN)
            .requestMatchers(HttpMethod.PUT, API_MODEL).hasRole(ADMIN)
            .requestMatchers(HttpMethod.DELETE, API_MODEL).hasRole(ADMIN)
             // Accès utilisateurs authentifié
            .requestMatchers(HttpMethod.PATCH, API_PERSONS).authenticated()
            .requestMatchers(HttpMethod.GET, API_PERSONS).authenticated()
            .requestMatchers("/api/**").authenticated()
            // Sécurité par défaut pour toute autre requête
            .anyRequest().authenticated()
                );

        // Injection du filtre JWT avant le filtre d'authentification standard par login/password
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * Expose le gestionnaire d'authentification pour être utilisé dans les services métier
     * (notamment dans AuthServiceImpl pour le processus de login).
     *
     * @param config Configuration d'authentification Spring.
     * @return L'AuthenticationManager prêt à l'emploi.
     * @throws BusinessException si la récupération échoue.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws BusinessException {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:4200", "https://co-voit.john-world.store"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}