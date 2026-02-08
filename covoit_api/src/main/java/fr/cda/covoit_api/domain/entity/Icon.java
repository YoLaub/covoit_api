package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "icon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Icon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_icon")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String label;
}