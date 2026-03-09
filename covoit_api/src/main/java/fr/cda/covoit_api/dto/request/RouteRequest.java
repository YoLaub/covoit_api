package fr.cda.covoit_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Objet de transfert de données pour la création ou la mise à jour complète d'un trajet.
 * Inclut la validation stricte des données géographiques et temporelles.
 *
 * @author Architecte Logiciel Senior
 */
@Data
public class RouteRequest {

    /**
     * Distance totale du trajet en kilomètres.
     * Doit être supérieure ou égale à 1.
     */
    @NotNull(message = "La distance est obligatoire")
    @Min(value = 1, message = "La distance doit être supérieure à 0")
    private Integer kms;

    /**
     * Nombre de places disponibles dans le véhicule (entre 1 et 8).
     */
    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    @Max(value = 8, message = "Maximum 8 places")
    private Short availableSeats;

    /**
     * Date prévue du trajet. Ne peut pas être dans le passé.
     */
    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date ne peut pas être dans le passé")
    private LocalDate tripDate;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime tripHour;

    @NotNull(message = "L'icône est obligatoire")
    private Integer iconId;

    /**
     * Adresse détaillée du point de départ.
     * @see AddressRequest
     */
    @NotNull(message = "L'adresse de départ est obligatoire")
    @Valid
    private AddressRequest startingAddress;

    @NotNull(message = "L'adresse d'arrivée est obligatoire")
    @Valid
    private AddressRequest arrivalAddress;

    /**
     * Classe interne représentant une adresse avec ses coordonnées GPS.
     */
    @Data
    public static class AddressRequest {

        /** Numéro de la rue */
        private String streetNumber;

        /** Nom de la rue */
        @NotBlank(message = "Le nom de rue est obligatoire")
        private String streetName;

        /**Code Postal sur 5 chiffres */
        @NotBlank(message = "Le code postal est obligatoire")
        @Pattern(regexp = "^\\d{5}$", message = "Le code postal doit contenir 5 chiffres")
        private String postalCode;

        /** Ville à renseigner en fonction de l'adresse. */
        @NotBlank(message = "La ville est obligatoire")
        private String city;

        /** Latitude décimale pour le positionnement cartographique. */
        @NotNull(message = "La latitude est obligatoire")
        private Double latitude;

        /** Longitude décimale pour le positionnement cartographique. */
        @NotNull(message = "La longitude est obligatoire")
        private Double longitude;
    }
}