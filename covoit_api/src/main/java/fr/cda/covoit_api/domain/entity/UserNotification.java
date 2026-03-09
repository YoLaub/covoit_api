package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entit&eacute; repr&eacute;sentant l'association entre un utilisateur et une notification.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code user_notification} en base de donn&eacute;es
 * et permet de g&eacute;rer l'envoi des notifications aux utilisateurs,
 * ainsi que leur statut de lecture.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see User
 * @see Notification
 */
@Entity
@Table(name = "user_notification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserNotification {

    /**
     * Identifiant unique de l'association utilisateur-notification.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_notification")
    private Integer id;

    /**
     * Utilisateur destinataire de la notification.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    private User user;

    /**
     * Notification associ&eacute;e &agrave; l'utilisateur.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code LAZY}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Notification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_notification", nullable = false)
    private Notification notification;

    /**
     * Date et heure de cr&eacute;ation de la notification pour l'utilisateur.
     * <p>Initialis&eacute; par d&eacute;faut &agrave; la date et l'heure courantes ({@link LocalDateTime#now()}).</p>
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Indicateur de lecture de la notification.
     * <p>
     * Vaut {@code false} par d&eacute;faut, passe &agrave; {@code true}
     * lorsque l'utilisateur a lu la notification.
     * </p>
     */
    @Column(name = "is_read")
    private Boolean isRead = false;
}