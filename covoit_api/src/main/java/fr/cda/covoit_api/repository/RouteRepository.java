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
    @Query("SELECT DISTINCT r FROM Route r " +
            "JOIN RouteLocation rlStart ON r.id = rlStart.route.id AND rlStart.type = 'starting' " +
            "JOIN RouteLocation rlEnd ON r.id = rlEnd.route.id AND rlEnd.type = 'arrival' " +
            "WHERE (:startCity IS NULL OR LOWER(CAST(rlStart.location.cityName AS string)) = LOWER(CAST(:startCity AS string))) " +
            "AND (:endCity IS NULL OR LOWER(CAST(rlEnd.location.cityName AS string)) = LOWER(CAST(:endCity AS string))) " +
            "AND (CAST(:tripDate AS date) IS NULL OR r.date = :tripDate)")
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