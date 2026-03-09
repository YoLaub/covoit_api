package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO pour la création ou la mise à jour des informations personnelles du profil.
 */
@Data
public class ProfilRequest {
    /** Prénom de l'utilisateur. */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstname;

    /** Nom de famille de l'utilisateur. */
    @NotBlank(message = "Le nom est obligatoire")
    private String lastname;

    /**
     * Numéro de téléphone au format français.
     * Supporte les formats : +33 ou 0 suivi de 9 chiffres.
     */
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^(\\+33|0)[1-9](\\d{2}){4}$", message = "Format de téléphone invalide")
    private String phone;
}