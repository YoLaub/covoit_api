package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entit&eacute; repr&eacute;sentant un type de notification.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code type_notif} en base de donn&eacute;es
 * et permet de cat&eacute;goriser les notifications envoy&eacute;es aux utilisateurs
 * (ex : information, alerte, rappel).
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see Notification
 */
@Entity
@Table(name = "type_notif")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TypeNotification {

    /**
     * Identifiant unique du type de notification.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_notif")
    private Integer id;

    /**
     * Libell&eacute; du type de notification.
     * <p>Ce champ est obligatoire et unique.</p>
     */
    @Column(nullable = false, unique = true)
    private String label;
}