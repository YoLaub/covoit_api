package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entit&eacute; repr&eacute;sentant un statut.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code status} en base de donn&eacute;es
 * et permet de d&eacute;finir les diff&eacute;rents statuts utilisables dans l'application
 * (ex : en attente, accept&eacute;, refus&eacute;, annul&eacute;).
 * </p>
 *
 * @author Covoit API
 * @version 1.0
 */
@Entity
@Table(name = "status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    /**
     * Identifiant unique du statut.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Integer id;

    /**
     * Libell&eacute; du statut.
     * <p>
     * Ce champ est obligatoire, unique et limit&eacute; &agrave; 50 caract&egrave;res.
     * </p>
     */
    @Column(unique = true, nullable = false, length = 50)
    private String label;
}