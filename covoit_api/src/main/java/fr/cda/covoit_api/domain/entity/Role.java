package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant un r&ocirc;le utilisateur.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code role_user} en base de donn&eacute;es
 * et permet de d&eacute;finir les diff&eacute;rents r&ocirc;les attribuables aux utilisateurs
 * de l'application (ex : administrateur, conducteur, passager).
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 */
@Entity
@Table(name = "role_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {

    /**
     * Identifiant unique du r&ocirc;le.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Integer id;

    /**
     * Libell&eacute; du r&ocirc;le.
     * <p>
     * Ce champ est obligatoire, unique et limit&eacute; &agrave; 50 caract&egrave;res.
     * </p>
     */
    @Column(unique = true, nullable = false, length = 50)
    private String label;
}