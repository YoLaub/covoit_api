package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entit&eacute; repr&eacute;sentant une marque de v&eacute;hicule.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code brand} en base de donn&eacute;es
 * et permet de g&eacute;rer les marques ainsi que leurs mod&egrave;les associ&eacute;s.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 */
@Entity
@Table(name = "brand")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    /**
     * Identifiant unique de la marque.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_brand")
    private Integer id;

    /**
     * Libell&eacute; de la marque.
     * <p>Ce champ est obligatoire et doit &ecirc;tre unique en base de donn&eacute;es.</p>
     */
    @Column(nullable = false, unique = true)
    private String label;

    /**
     * Liste des mod&egrave;les associ&eacute;s &agrave; cette marque.
     * <p>
     * La relation est de type {@code OneToMany} avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see Model
     */
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Model> models;
}
