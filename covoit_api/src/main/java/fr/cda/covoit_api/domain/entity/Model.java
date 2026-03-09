package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entit&eacute; repr&eacute;sentant un mod&egrave;le de v&eacute;hicule.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code model} en base de donn&eacute;es
 * et permet de g&eacute;rer les mod&egrave;les de v&eacute;hicules rattach&eacute;s &agrave; une marque.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see Brand
 */
@Entity
@Table(name = "model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Model {

    /**
     * Identifiant unique du mod&egrave;le.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_model")
    private Integer id;

    /**
     * Libell&eacute; du mod&egrave;le de v&eacute;hicule.
     * <p>Ce champ est obligatoire.</p>
     */
    @Column(nullable = false)
    private String label;

    /**
     * Marque &agrave; laquelle ce mod&egrave;le est rattach&eacute;.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Brand
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_brand", nullable = false)
    private Brand brand;
}