package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    /**
     * Recherche des trajets en filtrant par ville de départ, ville d'arrivée et date.
     * Cette requête effectue des jointures sur la table d'association RouteLocation.
     */
    @Query("SELECT r FROM Route r " +
            "JOIN RouteLocation rlStart ON r.id = rlStart.route.id " +
            "JOIN RouteLocation rlEnd ON r.id = rlEnd.route.id " +
            "WHERE rlStart.type = 'starting' AND rlStart.location.city = :startCity " +
            "AND rlEnd.type = 'arrival' AND rlEnd.location.city = :endCity " +
            "AND r.date = :tripDate")
    List<Route> findBySearchCriteria(
            @Param("startCity") String startCity,
            @Param("endCity") String endCity,
            @Param("tripDate") LocalDate tripDate
    );

    /**
     * Récupère tous les trajets créés par un conducteur spécifique
     */
    List<Route> findByDriverId(Integer driverId);
}