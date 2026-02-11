package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRouteId implements Serializable {
    @Column(name = "id_profil")
    private Integer profilId;

    @Column(name = "id_route")
    private Integer routeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRouteId that = (UserRouteId) o;
        return Objects.equals(profilId, that.profilId) && Objects.equals(routeId, that.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profilId, routeId);
    }
}