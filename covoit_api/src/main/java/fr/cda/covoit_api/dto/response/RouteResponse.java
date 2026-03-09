package fr.cda.covoit_api.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représentation simplifiée d'un trajet pour l'affichage en liste ou détail.
 * Agrège les informations du conducteur et les localisations formatées.
 */
@Data
public class RouteResponse {
    /** Identifiant unique du trajet en base. */
    private Integer id;
    /** Distance entre les deux points depart/arrivé */
    private Integer kms;
    /** Nomre de place disponible en base. */
    private Short availableSeats;
    /** Date du trajet en base. */
    private LocalDate date;
    /** Heure du trajet en base. */
    private LocalTime hour;
    /** Libellé de la préférence de confort (ex: "Non-fumeur"). */
    private String iconLabel;
    /** Nom complet du conducteur (Concaténation Prénom + Nom). */
    private String driverName;
    /** Détails du point de départ. @see LocationResponse */
    private LocationResponse departure;
    /** Détails du point d'arrivée. @see LocationResponse */
    private LocationResponse arrival;
}