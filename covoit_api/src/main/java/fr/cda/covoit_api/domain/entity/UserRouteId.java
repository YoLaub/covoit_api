package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Cl&eacute; primaire composite pour l'entit&eacute; {@link UserRoute}.
 * <p>
 * Cette classe impl&eacute;mente {@link Serializable} et est annot&eacute;e {@link Embeddable}
 * afin de servir de cl&eacute; composite embarqu&eacute;e, compos&eacute;e de l'identifiant
 * du profil et de l'identifiant du trajet.
 * </p>
 * <p>
 * Les m&eacute;thodes {@code equals} et {@code hashCode} sont red&eacute;finies,
 * ce qui est obligatoire pour toute cl&eacute; composite JPA.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see UserRoute
 */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRouteId implements Serializable {

    /**
     * Identifiant du profil faisant partie de la cl&eacute; composite.
     */
    @Column(name = "id_profil")
    private Integer profilId;

    /**
     * Identifiant du trajet faisant partie de la cl&eacute; composite.
     */
    @Column(name = "id_route")
    private Integer routeId;

    /**
     * V&eacute;rifie l'&eacute;galit&eacute; entre deux objets {@code UserRouteId}.
     * <p>
     * Deux cl&eacute;s sont consid&eacute;r&eacute;es &eacute;gales si elles poss&egrave;dent
     * le m&ecirc;me {@code profilId} et le m&ecirc;me {@code routeId}.
     * </p>
     *
     * @param o l'objet &agrave; comparer
     * @return {@code true} si les deux cl&eacute;s sont &eacute;gales, {@code false} sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRouteId that = (UserRouteId) o;
        return Objects.equals(profilId, that.profilId) && Objects.equals(routeId, that.routeId);
    }

    /**
     * Calcule le code de hachage de la cl&eacute; composite.
     * <p>
     * Le calcul est bas&eacute; sur les champs {@code profilId} et {@code routeId}.
     * </p>
     *
     * @return le code de hachage de l'objet
     */
    @Override
    public int hashCode() {
        return Objects.hash(profilId, routeId);
    }
}