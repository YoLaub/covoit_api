package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Entit&eacute; repr&eacute;sentant un trajet de covoiturage.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code route} en base de donn&eacute;es
 * et contient les informations relatives &agrave; un trajet : date, heure, nombre de places,
 * distance, ainsi que les relations vers le conducteur, les localisations,
 * les passagers et l'historique.
 * </p>
 *
 * @author Covoit API
 * @version 1.0
 * @see Profil
 * @see Icon
 * @see RouteLocation
 * @see UserRoute
 * @see Historical
 */
@Entity
@Table(name = "route")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Route {

    /**
     * Identifiant unique du trajet.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_route")
    private Integer id;

    /**
     * Nombre de places disponibles pour le trajet.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private Short place;

    /**
     * Date du trajet.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(name = "date_", nullable = false)
    private LocalDate date;

    /**
     * Heure de d&eacute;part du trajet.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(name = "hour_", nullable = false)
    private LocalTime hour;

    /**
     * Distance du trajet en kilom&egrave;tres.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private Integer distance;

    /**
     * Ic&ocirc;ne associ&eacute;e au trajet.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Icon
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_icon", nullable = false)
    private Icon icon;

    /**
     * Profil du conducteur associ&eacute; &agrave; ce trajet.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Profil
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profil", nullable = false)
    private Profil driver;

    /**
     * Liste des localisations (&eacute;tapes) associ&eacute;es &agrave; ce trajet.
     * <p>
     * Relation {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see RouteLocation
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteLocation> locations;

    /**
     * Liste des passagers inscrits sur ce trajet.
     * <p>
     * Relation {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see UserRoute
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoute> passengers;

    /**
     * Liste des entr&eacute;es d'historique associ&eacute;es &agrave; ce trajet.
     * <p>
     * Relation {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see Historical
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Historical> historicals;
}