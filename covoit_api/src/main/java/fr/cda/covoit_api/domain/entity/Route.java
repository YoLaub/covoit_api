package fr.cda.covoit_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "route")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_route")
    private Integer id;

    @Column(nullable = false)
    private Short place; // nb de places

    @Column(name = "date_", nullable = false)
    private LocalDate date;

    @Column(name = "hour_", nullable = false)
    private LocalTime hour;

    @Column(nullable = false)
    private Integer distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_icon", nullable = false)
    private Icon icon;
}