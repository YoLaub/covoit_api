package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_vehicule")
    private Integer id;

    @Column(nullable = false)
    private Short seats;

    @Column(unique = true)
    private String carregistration;

    @Column(name = "additional_info")
    private String additionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_model", nullable = false)
    private Model model;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_profil", nullable = false, unique = true)
    private Profil owner;
}