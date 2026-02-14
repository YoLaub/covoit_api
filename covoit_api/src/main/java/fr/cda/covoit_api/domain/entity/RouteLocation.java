package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RouteLocation {

    @EmbeddedId
    private RouteLocationId id;

    @ManyToOne
    @MapsId("routeId")
    @JoinColumn(name = "id_route")
    private Route route;

    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "id_location")
    private Location location;

    @Column(nullable = false)
    private String type;
}