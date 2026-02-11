package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import java.time.LocalDate;
import java.util.List;

public interface IRouteService {
    Route createRoute(Route route, Location start, Location end, String email);
    List<Route> searchRoutes(String startingCity, String arrivalCity, LocalDate tripDate);
    Route getById(Integer id);
    void deleteRoute(Integer id, String email);
}