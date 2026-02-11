package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String label;
}
