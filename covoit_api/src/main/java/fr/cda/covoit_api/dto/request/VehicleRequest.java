package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehicleRequest {
    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    @Max(value = 9, message = "Maximum 9 places")
    private Short seats;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}-[A-Z]{2}$", message = "Format d'immatriculation invalide (ex: AA-123-BB)")
    private String carregistration;

    private String additionalInfo;

    @NotNull(message = "L'ID du modèle est obligatoire")
    private Integer modelId;
}