package fr.cda.covoit_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RouteRequest {

    @NotNull(message = "La distance est obligatoire")
    @Min(value = 1, message = "La distance doit être supérieure à 0")
    private Integer kms;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    @Max(value = 8, message = "Maximum 8 places")
    private Short availableSeats;

    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date ne peut pas être dans le passé")
    private LocalDate tripDate;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime tripHour;

    @NotNull(message = "L'icône est obligatoire")
    private Integer iconId;

    @NotNull(message = "L'adresse de départ est obligatoire")
    @Valid
    private AddressRequest startingAddress;

    @NotNull(message = "L'adresse d'arrivée est obligatoire")
    @Valid
    private AddressRequest arrivalAddress;

    @Data
    public static class AddressRequest {

        private String streetNumber;

        @NotBlank(message = "Le nom de rue est obligatoire")
        private String streetName;

        @NotBlank(message = "Le code postal est obligatoire")
        @Pattern(regexp = "^\\d{5}$", message = "Le code postal doit contenir 5 chiffres")
        private String postalCode;

        @NotBlank(message = "La ville est obligatoire")
        private String city;

        @NotNull(message = "La latitude est obligatoire")
        private Double latitude;

        @NotNull(message = "La longitude est obligatoire")
        private Double longitude;
    }
}