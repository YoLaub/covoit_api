package fr.cda.covoit_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration centrale pour la documentation interactive de l'API (Swagger/OpenAPI).
 * Permet de définir les métadonnées de l'API et de configurer l'interface de test
 * pour supporter l'authentification par jeton JWT.
 *
 * @author Yoann Laubert
 */
@Configuration
public class OpenApiConfig {

    /**
     * Définit l'objet OpenAPI global pour l'application.
     * Configure :
     * - Le titre et la description de la documentation.
     * - Le schéma de sécurité global (Bearer Token) pour permettre aux développeurs
     *   de tester les routes protégées directement depuis l'interface Swagger UI.
     *
     * @return Une instance configurée d'OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                .info(new Info()
                        .title("Covoit GRETA API")
                        .version("1.0")
                        .description("Documentation de l'API de Covoiturage"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre jeton JWT")));
    }
}