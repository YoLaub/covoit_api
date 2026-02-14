package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.RouteLocation;
import fr.cda.covoit_api.domain.entity.RouteLocationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteLocationRepository extends JpaRepository<RouteLocation, RouteLocationId> {

    List<RouteLocation> findByIdRouteId(Integer routeId);
}