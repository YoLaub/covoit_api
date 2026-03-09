package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO pour l'enregistrement d'un véhicule.
 * Valide le format des plaques d'immatriculation françaises SIV.
 */
@Data
public class VehicleRequest {

    /**
     * Nombre de places assises totales (incluant le conducteur).
     */
    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    @Max(value = 9, message = "Maximum 9 places")
    private Short seats;

    /**
     * Immatriculation au format AA-123-BB.
     */
    @NotBlank(message = "L'immatriculation est obligatoire")
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$", message = "Format d'immatriculation invalide (ex: AA-123-BB)")
    private String carregistration;

    /** Commentaire additionnel sur le vehicule */
    private String additionalInfo;

    /** Identifiant du modèle lié en base. */
    @NotNull(message = "L'ID du modèle est obligatoire")
    private Integer modelId;
}