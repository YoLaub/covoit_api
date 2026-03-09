package fr.cda.covoit_api.dto.request;

import lombok.Data;

/**
 * Objet de transfert pour les identifiants de connexion.
 */
@Data
public class LoginRequest {
    /** Email servant d'identifiant unique. */
    private String email;
    /** Mot de passe utilisateur en clair (hashé par le serveur lors du traitement). */
    private String password;
}