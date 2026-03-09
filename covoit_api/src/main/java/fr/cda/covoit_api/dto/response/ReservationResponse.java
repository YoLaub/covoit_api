package fr.cda.covoit_api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO détaillant l'état d'une réservation effectuée par un passager.
 */
@Data
public class ReservationResponse {

    private Integer routeId;
    /** État de la réservation : "confirmed" ou "cancelled". */
    private String status;
    /** Horodatage de la création de la demande. */
    private LocalDateTime createdAt;
    /** Ville de départ récupérée via la table de jointure RouteLocation. */
    private String departureCity;
    /** Ville de d'arrivé récupérée via la table de jointure RouteLocation. */
    private String arrivalCity;
    /** date du trajet. */
    private String tripDate;
    /** Nom du conducteur */
    private String driverName;
}