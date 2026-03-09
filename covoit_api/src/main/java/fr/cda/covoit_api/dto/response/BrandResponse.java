package fr.cda.covoit_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Représentation simplifiée d'une marque pour les listes de sélection.
 */
@Data
@AllArgsConstructor
public class BrandResponse {
    /** Identifiant technique en base de données. */
    private Integer id;
    /** Nom de la marque. */
    private String label;
}