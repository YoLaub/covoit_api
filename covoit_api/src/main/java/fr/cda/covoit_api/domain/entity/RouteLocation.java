package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant l'association entre un trajet et une localisation.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code route_location} en base de donn&eacute;es
 * et mod&eacute;lise la relation {@code ManyToMany} entre {@link Route} et {@link Location}
 * avec un attribut suppl&eacute;mentaire {@code type} (ex : d&eacute;part, arriv&eacute;e, &eacute;tape).
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see Route
 * @see Location
 * @see RouteLocationId
 */
@Entity
@Table(name = "route_location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RouteLocation {

    /**
     * Cl&eacute; primaire composite de l'association.
     *
     * @see RouteLocationId
     */
    @EmbeddedId
    private RouteLocationId id;

    /**
     * Trajet associ&eacute; &agrave; cette localisation.
     * <p>
     * Relation {@code ManyToOne} mapp&eacute;e sur la partie {@code routeId}
     * de la cl&eacute; composite.
     * </p>
     *
     * @see Route
     */
    @ManyToOne
    @MapsId("routeId")
    @JoinColumn(name = "id_route")
    private Route route;

    /**
     * Localisation associ&eacute;e &agrave; ce trajet.
     * <p>
     * Relation {@code ManyToOne} mapp&eacute;e sur la partie {@code locationId}
     * de la cl&eacute; composite.
     * </p>
     *
     * @see Location
     */
    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "id_location")
    private Location location;

    /**
     * Type de la localisation dans le trajet.
     * <p>
     * Ce champ est obligatoire et indique le r&ocirc;le de la localisation
     * (ex : d&eacute;part, arriv&eacute;e, &eacute;tape interm&eacute;diaire).
     * </p>
     */
    @Column(nullable = false)
    private String type;
}