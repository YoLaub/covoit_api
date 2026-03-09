package fr.cda.covoit_api.dto.response;

import lombok.Data;

/**
 * Détails d'une adresse géographique retournés pour l'affichage des trajets.
 */
@Data
public class LocationResponse {
    /** Numéro de voie (facultatif). */
    private String streetNumber;
    /** Nom de la rue ou avenue. */
    private String streetName;
    /** Code postal à 5 chiffres. */
    private String postalCode;
    /** Nom de la commune. */
    private String cityName;
}