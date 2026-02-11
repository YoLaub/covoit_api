package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_notif")
    private TypeNotification type;
}