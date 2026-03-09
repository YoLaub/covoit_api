package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité d'association représentant une réservation entre un passager et un trajet.
 * Gère le cycle de vie de la participation d'un utilisateur (confirmé, annulé, etc.).
 */
@Entity
@Table(name = "user_route")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRoute {

    /**
     * Clé composite encapsulant l'ID du profil et l'ID du trajet.
     * @see fr.cda.covoit_api.domain.entity.UserRouteId
     */
    @EmbeddedId
    private UserRouteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profilId")
    @JoinColumn(name = "id_profil")
    private Profil passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("routeId")
    @JoinColumn(name = "id_route")
    private Route route;

    /**
     * Rôle de l'utilisateur dans ce trajet.
     * Par défaut "passenger".
     */
    @Column(name = "role_in_route", nullable = false)
    private String roleInRoute = "passenger";

    /**
     * Statut actuel de la réservation (ex: "confirmed", "cancelled").
     * Défini par défaut à "pending".
     */
    @Column(nullable = false)
    private String status = "pending"; // pending, confirmed, cancelled

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}