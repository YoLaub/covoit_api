package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant une ic&ocirc;ne.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code icon} en base de donn&eacute;es
 * et permet de g&eacute;rer les ic&ocirc;nes utilis&eacute;es dans l'application.
 * </p>
 *
 * @author Covoit API
 * @version 1.0
 */
@Entity
@Table(name = "icon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Icon {

    /**
     * Identifiant unique de l'ic&ocirc;ne.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_icon")
    private Integer id;

    /**
     * Libell&eacute; de l'ic&ocirc;ne.
     * <p>
     * Ce champ est obligatoire, unique et limit&eacute; &agrave; 50 caract&egrave;res.
     * </p>
     */
    @Column(nullable = false, unique = true, length = 50)
    private String label;
}