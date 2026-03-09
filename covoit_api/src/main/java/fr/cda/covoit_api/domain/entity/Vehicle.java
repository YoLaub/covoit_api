package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entit&eacute; repr&eacute;sentant un v&eacute;hicule.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code vehicule} en base de donn&eacute;es
 * et contient les informations relatives &agrave; un v&eacute;hicule : nombre de places,
 * immatriculation, mod&egrave;le et propri&eacute;taire.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see Model
 * @see Profil
 */
@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    /**
     * Identifiant unique du v&eacute;hicule.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_vehicule")
    private Integer id;

    /**
     * Nombre de places assises du v&eacute;hicule.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private Short seats;

    /**
     * Num&eacute;ro d'immatriculation du v&eacute;hicule.
     * <p>Ce champ est unique et facultatif.</p>
     */
    @Column(unique = true)
    private String carregistration;

    /**
     * Informations compl&eacute;mentaires sur le v&eacute;hicule.
     * <p>Ce champ est facultatif (ex : couleur, &eacute;quipements, particularit&eacute;s).</p>
     */
    @Column(name = "additional_info")
    private String additionalInfo;

    /**
     * Mod&egrave;le du v&eacute;hicule.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Model
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_model", nullable = false)
    private Model model;

    /**
     * Propri&eacute;taire du v&eacute;hicule.
     * <p>
     * Relation {@code OneToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire et unique ({@code nullable = false, unique = true}),
     * garantissant qu'un profil ne peut poss&eacute;der qu'un seul v&eacute;hicule.
     * </p>
     *
     * @see Profil
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_profil", nullable = false, unique = true)
    private Profil owner;
}