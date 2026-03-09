package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entit&eacute; repr&eacute;sentant le profil d'un utilisateur.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code user_profil} en base de donn&eacute;es
 * et contient les informations personnelles de l'utilisateur ainsi que ses relations
 * avec son compte, son v&eacute;hicule, ses trajets en tant que conducteur
 * et ses r&eacute;servations en tant que passager.
 * </p>
 *
 * @author Covoit API
 * @version 1.0
 * @see User
 * @see Vehicle
 * @see Route
 * @see UserRoute
 */
@Entity
@Table(name = "user_profil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profil {

    /**
     * Identifiant unique du profil.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_profil")
    private Integer id;

    /**
     * Pr&eacute;nom de l'utilisateur.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private String firstname;

    /**
     * Nom de famille de l'utilisateur.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private String lastname;

    /**
     * Num&eacute;ro de t&eacute;l&eacute;phone de l'utilisateur.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private String phone;

    /**
     * Compte utilisateur associ&eacute; &agrave; ce profil.
     * <p>
     * Relation {@code OneToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see User
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_account", nullable = false)
    private User user;

    /**
     * V&eacute;hicule poss&eacute;d&eacute; par l'utilisateur.
     * <p>
     * Relation {@code OneToOne} invers&eacute;e avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see Vehicle
     */
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle vehicle;

    /**
     * Liste des trajets cr&eacute;&eacute;s par l'utilisateur en tant que conducteur.
     * <p>
     * Relation {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see Route
     */
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Route> routes;

    /**
     * Liste des r&eacute;servations de l'utilisateur en tant que passager.
     * <p>
     * Relation {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see UserRoute
     */
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoute> reservations;
}