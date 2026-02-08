package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RouteLocationId implements Serializable {

    @Column(name = "id_route")
    private Integer routeId;

    @Column(name = "id_location")
    private Integer locationId;

    // equals et hashCode sont obligatoires pour une clé composite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteLocationId that = (RouteLocationId) o;
        return Objects.equals(routeId, that.routeId) &&
                Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, locationId);
    }
}