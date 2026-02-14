package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements IRouteService {

    private static final String STARTING = "starting";
    private static final String ARRIVAL = "arrival";

    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final RouteLocationRepository routeLocationRepository;
    private final ProfilRepository profilRepository;

    @Override
    @Transactional
    public Route createRoute(Route route, Location start, Location end, String email) {
        Profil driver = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("Profil introuvable", HttpStatus.NOT_FOUND));
        route.setDriver(driver);

        Location savedStart = locationRepository.save(start);
        Location savedEnd = locationRepository.save(end);
        Route savedRoute = routeRepository.save(route);

        RouteLocation startLink = new RouteLocation(
                new RouteLocationId(savedRoute.getId(), savedStart.getId()),
                savedRoute, savedStart, STARTING
        );
        routeLocationRepository.save(startLink);

        RouteLocation endLink = new RouteLocation(
                new RouteLocationId(savedRoute.getId(), savedEnd.getId()),
                savedRoute, savedEnd, ARRIVAL
        );
        routeLocationRepository.save(endLink);

        return savedRoute;
    }

    @Override
    public List<Route> searchRoutes(String startingCity, String arrivalCity, LocalDate tripDate) {
        return routeRepository.findBySearchCriteria(startingCity, arrivalCity, tripDate);
    }

    @Override
    public Route getById(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Trajet introuvable", HttpStatus.NOT_FOUND));
    }

    @Override
    public Map<String, Location> getLocationsForRoute(Integer routeId) {
        List<RouteLocation> links = routeLocationRepository.findByIdRouteId(routeId);
        Map<String, Location> locations = new HashMap<>();
        for (RouteLocation link : links) {
            locations.put(link.getType(), link.getLocation());
        }
        return locations;
    }

    @Override
    @Transactional
    public void deleteRoute(Integer id, String email) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Trajet introuvable", HttpStatus.NOT_FOUND));

        if (!route.getDriver().getUser().getEmail().equals(email)) {
            throw new BusinessException("Vous n'êtes pas le conducteur de ce trajet", HttpStatus.FORBIDDEN);
        }

        routeRepository.deleteById(id);
    }
}