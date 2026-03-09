package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de requête pour la création ou la mise à jour d'une marque de véhicule.
 */
@Data
public class BrandRequest {

    /**
     * Libellé unique de la marque (ex: "Peugeot", "Tesla").
     * Contrainte : Ne peut être vide ou null.
     */
    @NotBlank(message = "Le nom de la marque est obligatoire")
    private String label;
}
