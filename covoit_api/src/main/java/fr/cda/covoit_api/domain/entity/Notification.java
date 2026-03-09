package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant une notification.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code notification} en base de donn&eacute;es
 * et permet de g&eacute;rer les notifications envoy&eacute;es aux utilisateurs de l'application.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see TypeNotification
 */
@Entity
@Table(name = "notification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {

    /**
     * Identifiant unique de la notification.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer id;

    /**
     * Contenu de la notification.
     * <p>
     * Ce champ est obligatoire et de type {@code TEXT},
     * permettant de stocker un message de longueur variable.
     * </p>
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contain;

    /**
     * Type de la notification.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY},
     * permettant de cat&eacute;goriser la notification.
     * </p>
     *
     * @see TypeNotification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_notif")
    private TypeNotification type;
}