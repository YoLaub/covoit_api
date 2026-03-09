package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant une localisation g&eacute;ographique.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code location} en base de donn&eacute;es
 * et permet de stocker les informations d'adresse ainsi que les coordonn&eacute;es GPS
 * associ&eacute;es &agrave; un lieu.
 * </p>
 *
 * @author Laubert Yoann
 * @version 1.0
 */
@Entity
@Table(name = "location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    /**
     * Identifiant unique de la localisation.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_location")
    private Integer id;

    /**
     * Num&eacute;ro de rue de l'adresse.
     * <p>Ce champ est facultatif.</p>
     */
    @Column(name = "street_number")
    private String streetNumber;

    /**
     * Nom de la rue.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(name = "street_name", nullable = false)
    private String streetName;

    /**
     * Code postal de la localisation.
     * <p>Ce champ est obligatoire et limit&eacute; &agrave; 10 caract&egrave;res.</p>
     */
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    /**
     * Nom de la ville.
     * <p>Ce champ est obligatoire et limit&eacute; &agrave; 100 caract&egrave;res.</p>
     */
    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    /**
     * Latitude de la localisation (coordonn&eacute;e GPS).
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private Double latitude;

    /**
     * Longitude de la localisation (coordonn&eacute;e GPS).
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private Double longitude;
}