package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entit&eacute; repr&eacute;sentant un compte utilisateur.
 * <p>
 * Cette classe est mapp&eacute;e sur la table {@code user_account} en base de donn&eacute;es
 * et contient les informations d'authentification et de s&eacute;curit&eacute; du compte,
 * ainsi que les relations vers le statut, le r&ocirc;le et le profil de l'utilisateur.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see Status
 * @see Role
 * @see Profil
 */
@Entity
@Table(name = "user_account")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    /**
     * Identifiant unique du compte utilisateur.
     * <p>G&eacute;n&eacute;r&eacute; automatiquement par la base de donn&eacute;es (strat&eacute;gie {@code IDENTITY}).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_account")
    private Integer id;

    /**
     * Adresse e-mail de l'utilisateur.
     * <p>Ce champ est obligatoire et unique.</p>
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe de l'utilisateur.
     * <p>Ce champ est obligatoire et doit &ecirc;tre stock&eacute; sous forme hach&eacute;e.</p>
     */
    @Column(nullable = false)
    private String password;

    /**
     * Jeton d'authentification de l'utilisateur.
     * <p>Ce champ est unique et facultatif.</p>
     */
    @Column(unique = true)
    private String token;

    /**
     * Jeton de r&eacute;initialisation du mot de passe.
     * <p>Ce champ est unique et facultatif, utilis&eacute; lors de la proc&eacute;dure
     * de r&eacute;initialisation du mot de passe.</p>
     */
    @Column(name = "reset_password_token", unique = true)
    private String resetPasswordToken;

    /**
     * Date d'expiration du jeton de r&eacute;initialisation du mot de passe.
     * <p>Au-del&agrave; de cette date, le jeton n'est plus valide.</p>
     */
    @Column(name = "reset_password_expires_at")
    private LocalDate resetPasswordExpiresAt;

    /**
     * Date et heure de la derni&egrave;re connexion de l'utilisateur.
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Statut du compte utilisateur.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code EAGER}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Status
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    /**
     * R&ocirc;le attribu&eacute; &agrave; l'utilisateur.
     * <p>
     * Relation {@code ManyToOne} charg&eacute;e en mode {@code EAGER}.
     * Ce champ est obligatoire ({@code nullable = false}).
     * </p>
     *
     * @see Role
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    /**
     * Profil associ&eacute; au compte utilisateur.
     * <p>
     * Relation {@code OneToOne} invers&eacute;e avec suppression en cascade
     * et suppression des orphelins activ&eacute;e ({@code orphanRemoval = true}).
     * </p>
     *
     * @see Profil
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profil profil;
}