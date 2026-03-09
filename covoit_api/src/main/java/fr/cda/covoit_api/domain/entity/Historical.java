package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entit&eacute; repr&eacute;sentant un historique li&eacute; &agrave; un trajet.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code historical} en base de donn&eacute;es
 * et permet de conserver un historique de commentaires associ&eacute;s &agrave; un trajet.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 */
@Entity
@Table(name = "historical")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Historical {

    /**
     * Identifiant unique de l'entr&eacute;e d'historique.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historical")
    private Integer id;

    /**
     * Commentaire associ&eacute; &agrave; l'entr&eacute;e d'historique.
     * <p>Champ de type {@code TEXT} permettant de stocker un commentaire de longueur variable.</p>
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * Date et heure de cr&eacute;ation de l'entr&eacute;e d'historique.
     * <p>
     * Ce champ est obligatoire et est initialis&eacute; par d&eacute;faut
     * &agrave; la date et l'heure courantes ({@link LocalDateTime#now()}).
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Trajet associ&eacute; &agrave; cette entr&eacute;e d'historique.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Route
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_route", nullable = false)
    private Route route;
}