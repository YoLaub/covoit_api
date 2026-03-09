package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de création de compte utilisateur (Phase d'inscription initiale).
 */
@Data
public class RegisterRequest {
    /** Identifiant de connexion. Doit être un email valide. */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    /**
     * Mot de passe de sécurité.
     * Contrainte : Minimum 8 caractères pour respecter les standards de sécurité.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
}