package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_route")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRoute {
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

    @Column(name = "role_in_route", nullable = false)
    private String roleInRoute = "passenger";

    @Column(nullable = false)
    private String status = "pending"; // pending, confirmed, cancelled

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}