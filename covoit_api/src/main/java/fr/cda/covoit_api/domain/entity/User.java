package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.ObjectInputFilter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_account")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String token;

    @Column(name = "reset_password_token", unique = true)
    private String resetPasswordToken;

    @Column(name = "reset_password_expires_at")
    private LocalDate resetPasswordExpiresAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;
}