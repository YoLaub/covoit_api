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

    // Injection par constructeur (Recommandé par rapport à @Autowired)
    public RouteController(IRouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Recherche de trajets filtrés.
     */
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
     */
    @PostMapping
    public ResponseEntity<Route> create(@RequestBody RouteRequest dto, Principal principal) {
        // 1. Mapping manuel du DTO vers l'entité Route
        Route route = new Route();
        route.setPlace(dto.getAvailableSeats());
        route.setDate(dto.getTripDate());
        route.setHour(dto.getTripHour());
        route.setDistance(dto.getKms());

        // 2. Mapping manuel des adresses (Location)
        // Note: On concatène le numéro et la rue pour le champ 'address' de l'entité
        Location start = new Location(
                null,
                dto.getStartingAddress().getStreetNumber() + " " + dto.getStartingAddress().getStreetName(),
                dto.getStartingAddress().getCity(),
                dto.getStartingAddress().getPostalCode(),
                dto.getStartingAddress().getLatitude(),
                dto.getStartingAddress().getLongitude()
        );

        Location end = new Location(
                null,
                dto.getArrivalAddress().getStreetNumber() + " " + dto.getArrivalAddress().getStreetName(),
                dto.getArrivalAddress().getCity(),
                dto.getArrivalAddress().getPostalCode(),
                dto.getArrivalAddress().getLatitude(),
                dto.getArrivalAddress().getLongitude()
        );

        // 3. Appel au service avec l'email du conducteur (issu du token JWT)
        Route saved = routeService.createRoute(route, start, end, principal.getName());

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Récupérer le détail d'un trajet par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Route> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(routeService.getById(id));
    }

    /**
     * Supprimer un trajet (Seul le conducteur ou l'Admin peut faire ça).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        routeService.deleteRoute(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}