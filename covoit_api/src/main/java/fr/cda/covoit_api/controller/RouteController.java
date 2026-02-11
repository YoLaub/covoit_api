package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class RouteController {

    private final IRouteService routeService;

    public RouteController(IRouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<Route>> search(
            @RequestParam(required = false) String startingcity,
            @RequestParam(required = false) String arrivalcity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripdate) {

        List<Route> routes = routeService.searchRoutes(startingcity, arrivalcity, tripdate);
        return ResponseEntity.ok(routes);
    }

    /**
     * Publication d'un trajet par un conducteur.
     * Mise à jour pour utiliser les champs détaillés de l'entité Location.
     */
    @PostMapping
    public ResponseEntity<Route> create(@RequestBody RouteRequest dto, Principal principal) {
        // 1. Mapping du DTO vers l'entité Route
        Route route = new Route();
        route.setPlace(dto.getAvailableSeats());
        route.setDate(dto.getTripDate());
        route.setHour(dto.getTripHour());
        route.setDistance(dto.getKms());

        // 2. Mapping vers l'entité Location (Départ)
        // Utilisation des setters de Location.java conformément au nouveau schéma
        Location start = new Location();
        start.setStreetNumber(dto.getStartingAddress().getStreetNumber());
        start.setStreetName(dto.getStartingAddress().getStreetName());
        start.setPostalCode(dto.getStartingAddress().getPostalCode());
        start.setCityName(dto.getStartingAddress().getCity()); // Correspond à cityName dans Location.java
        start.setLatitude(dto.getStartingAddress().getLatitude());
        start.setLongitude(dto.getStartingAddress().getLongitude());

        // 3. Mapping vers l'entité Location (Arrivée)
        Location end = new Location();
        end.setStreetNumber(dto.getArrivalAddress().getStreetNumber());
        end.setStreetName(dto.getArrivalAddress().getStreetName());
        end.setPostalCode(dto.getArrivalAddress().getPostalCode());
        end.setCityName(dto.getArrivalAddress().getCity());
        end.setLatitude(dto.getArrivalAddress().getLatitude());
        end.setLongitude(dto.getArrivalAddress().getLongitude());

        // 4. Appel au service avec les objets Location complets
        Route saved = routeService.createRoute(route, start, end, principal.getName());

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(routeService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        routeService.deleteRoute(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}