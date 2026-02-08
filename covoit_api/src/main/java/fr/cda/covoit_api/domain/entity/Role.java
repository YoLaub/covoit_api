package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String label;
}