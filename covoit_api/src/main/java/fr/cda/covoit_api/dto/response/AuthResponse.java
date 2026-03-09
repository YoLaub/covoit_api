package fr.cda.covoit_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet retourné suite à une authentification réussie.
 * Contient le Bearer Token nécessaire pour les requêtes ultérieures.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    /** Token JWT (Json Web Token) à inclure dans le header 'Authorization'. */
    private String token;
    /** Email de l'utilisateur pour la gestion du profil côté client. */
    private String email;
    /** Rôle principal de l'utilisateur (ADMIN, USER) pour le contrôle d'UI. */
    private String role;
}
