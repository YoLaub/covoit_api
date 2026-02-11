package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_notif")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TypeNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_notif")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String label;
}