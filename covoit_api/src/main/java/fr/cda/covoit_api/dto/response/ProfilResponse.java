package fr.cda.covoit_api.dto.response;

import lombok.Data;

/**
 * Informations de profil public retournées par l'API.
 * Note : Le mot de passe et les tokens ne sont jamais inclus ici.
 */
@Data
public class ProfilResponse {
    /** ID unique du profil. */
    private Integer id;
    /** Prenom récupéré depuis l'entité User liée. */
    private String firstname;
    /** Nom de famille récupéré depuis l'entité User liée. */
    private String lastname;
    /** Téléphone récupéré depuis l'entité User liée. */
    private String phone;
    /** Email récupéré depuis l'entité User liée. */
    private String email;
}
