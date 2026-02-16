package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Icon;
import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.IconRepository;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Trajets", description = "Gestion des annonces de covoiturage")
@RequiredArgsConstructor
public class RouteController {

    private final IRouteService routeService;
    private final EntityMapper entityMapper;
    private final IconRepository iconRepository;
    private static final String ARRIVAL = "arrival";
    private static final String STARTING = "starting";

    @Operation(summary = "Rechercher des trajets", description = "Permet de filtrer par ville et date")
    @GetMapping
    public ResponseEntity<List<RouteResponse>> search(
            @RequestParam(required = false) String startingcity,
            @RequestParam(required = false) String arrivalcity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripdate) {

        return ResponseEntity.ok(routeService.searchRoutesWithDetails(startingcity, arrivalcity, tripdate));
    }

    @PostMapping
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest dto, Principal principal) {
        Location start = entityMapper.toLocation(dto.getStartingAddress());
        Location end = entityMapper.toLocation(dto.getArrivalAddress());
        Route route = entityMapper.toRoute(dto);

        Icon icon = iconRepository.findById(dto.getIconId())
                .orElseThrow(() -> new BusinessException("Icône de préférence non trouvée", HttpStatus.NOT_FOUND));
        route.setIcon(icon);

        Route saved = routeService.createRoute(route, start, end, principal.getName());

        return new ResponseEntity<>(
                entityMapper.toRouteResponse(saved, start, end),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RouteResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody RouteRequest dto,
            Principal principal) {

        Route updated = routeService.updateRoute(id, dto, principal.getName());

        // Récupération des locations mises à jour pour la réponse
        Map<String, Location> locations = routeService.getLocationsForRoute(id);

        return ResponseEntity.ok(
                entityMapper.toRouteResponse(updated, locations.get(STARTING), locations.get(ARRIVAL))
        );
    }

    @PatchMapping("/{id}/seats")
    public ResponseEntity<RouteResponse> updateSeats(
            @PathVariable Integer id,
            @RequestBody Map<String, Short> body,
            Principal principal) {

        Short newSeats = body.get("availableSeats");
        if (newSeats == null || newSeats < 1) {
            throw new BusinessException("Le nombre de places doit être supérieur à 0", HttpStatus.BAD_REQUEST);
        }

        Route updated = routeService.updateRouteSeats(id, newSeats, principal.getName());

        // Pour le retour, on récupère les locations pour le mapper
        Map<String, Location> locations = routeService.getLocationsForRoute(id);
        return ResponseEntity.ok(entityMapper.toRouteResponse(updated, locations.get(STARTING), locations.get(ARRIVAL)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getById(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        Map<String, Location> locations = routeService.getLocationsForRoute(id);
        return ResponseEntity.ok(
                entityMapper.toRouteResponse(route, locations.get(STARTING), locations.get(ARRIVAL))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        routeService.deleteRoute(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}