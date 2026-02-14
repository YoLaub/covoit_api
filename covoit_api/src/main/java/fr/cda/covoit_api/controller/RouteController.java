package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.service.interfaces.IRouteService;
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
@RequiredArgsConstructor
public class RouteController {

    private final IRouteService routeService;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<List<RouteResponse>> search(
            @RequestParam(required = false) String startingcity,
            @RequestParam(required = false) String arrivalcity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripdate) {

        List<Route> routes = routeService.searchRoutes(startingcity, arrivalcity, tripdate);

        List<RouteResponse> responses = routes.stream().map(route -> {
            Map<String, Location> locations = routeService.getLocationsForRoute(route.getId());
            return entityMapper.toRouteResponse(route, locations.get("starting"), locations.get("arrival"));
        }).toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<RouteResponse> create(@RequestBody RouteRequest dto, Principal principal) {
        Location start = entityMapper.toLocation(dto.getStartingAddress());
        Location end = entityMapper.toLocation(dto.getArrivalAddress());
        Route route = entityMapper.toRoute(dto);

        Route saved = routeService.createRoute(route, start, end, principal.getName());

        return new ResponseEntity<>(
                entityMapper.toRouteResponse(saved, start, end),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getById(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        Map<String, Location> locations = routeService.getLocationsForRoute(id);
        return ResponseEntity.ok(
                entityMapper.toRouteResponse(route, locations.get("starting"), locations.get("arrival"))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        routeService.deleteRoute(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}