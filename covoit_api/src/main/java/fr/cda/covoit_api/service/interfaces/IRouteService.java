package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IRouteService {
    Route createRoute(Route route, Location start, Location end, String email);
    List<Route> searchRoutes(String startingCity, String arrivalCity, LocalDate tripDate);
    List<RouteResponse> searchRoutesWithDetails(String startingCity, String arrivalCity, LocalDate tripDate);
    Route getById(Integer id);
    void deleteRoute(Integer id, String email);
    Map<String, Location> getLocationsForRoute(Integer routeId);
    Route updateRouteSeats(Integer id, Short newCapacity, String email);
    Route updateRoute(Integer id, RouteRequest dto, String email);
}