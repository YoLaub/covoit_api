package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.RouteLocation;
import fr.cda.covoit_api.domain.entity.RouteLocationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteLocationRepository extends JpaRepository<RouteLocation, RouteLocationId> {}