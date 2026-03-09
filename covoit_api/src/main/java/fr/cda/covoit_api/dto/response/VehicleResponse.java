package fr.cda.covoit_api.dto.response;

import lombok.Data;

/**
 * Détails complets du véhicule d'un utilisateur.
 * Utilisé pour afficher les informations de voiture lors de la consultation d'un trajet.
 */
@Data
public class VehicleResponse {
    /** ID unique du véhicule. */
    private Integer id;
    /** Capacité totale de places assises. */
    private Short seats;
    /** Plaque d'immatriculation (format SIV : AA-123-BB). */
    private String carregistration;
    /** Informations optionnelles (couleur, type de coffre, etc.). */
    private String additionalInfo;
    /** Nom du modèle (extrait de l'entité Model). */
    private String modelName;
    /** Nom de la marque (extrait de l'entité Brand). */
    private String brandName;
}