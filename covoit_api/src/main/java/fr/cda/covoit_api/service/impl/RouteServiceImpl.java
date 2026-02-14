package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
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
    private static final String TRIP_NOT_FOUND = "Trajet introuvable";

    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final RouteLocationRepository routeLocationRepository;
    private final ProfilRepository profilRepository;
    private final IconRepository iconRepository;
    private final EntityMapper entityMapper;
    private final UserRouteRepository userRouteRepository;

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
    public List<RouteResponse> searchRoutesWithDetails(String startingCity, String arrivalCity, LocalDate tripDate) {
        List<Route> routes = routeRepository.findBySearchCriteria(startingCity, arrivalCity, tripDate);

        return routes.stream().map(route -> {
            Map<String, Location> locations = getLocationsForRoute(route.getId());
            return entityMapper.toRouteResponse(
                    route,
                    locations.get(STARTING),
                    locations.get(ARRIVAL)
            );
        }).toList();
    }

    @Override
    public Route getById(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TRIP_NOT_FOUND, HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new BusinessException(TRIP_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!route.getDriver().getUser().getEmail().equals(email)) {
            throw new BusinessException("Vous n'êtes pas le conducteur de ce trajet", HttpStatus.FORBIDDEN);
        }

        routeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Route updateRouteSeats(Integer id, Short newCapacity, String email) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TRIP_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Vérification de propriété
        if (!route.getDriver().getUser().getEmail().equals(email)) {
            throw new BusinessException("Seul le conducteur peut modifier ce trajet", HttpStatus.FORBIDDEN);
        }

        // Vérification cohérence métier : on ne peut pas descendre en dessous du nombre de réservations actuelles
        // Note: On injecte UserRouteRepository pour ce calcul
        long currentOccupied = userRouteRepository.countByRouteIdAndStatusNot(id, "cancelled");
        if (newCapacity < currentOccupied) {
            throw new BusinessException("Impossible de réduire la capacité en dessous du nombre de réservations actuelles (" + currentOccupied + ")", HttpStatus.CONFLICT);
        }

        route.setPlace(newCapacity);
        return routeRepository.save(route);
    }

    @Override
    @Transactional
    public Route updateRoute(Integer id, RouteRequest dto, String email) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TRIP_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!route.getDriver().getUser().getEmail().equals(email)) {
            throw new BusinessException("Action non autorisée", HttpStatus.FORBIDDEN);
        }

        // Vérification cohérence métier
        long currentOccupied = userRouteRepository.countByRouteIdAndStatusNot(id, "cancelled");
        if (dto.getAvailableSeats() < currentOccupied) {
            throw new BusinessException(
                    "Impossible de réduire la capacité en dessous du nombre de réservations actuelles (" + currentOccupied + ")",
                    HttpStatus.CONFLICT
            );
        }

        route.setDistance(dto.getKms());
        route.setPlace(dto.getAvailableSeats());
        route.setDate(dto.getTripDate());
        route.setHour(dto.getTripHour());

        Icon icon = iconRepository.findById(dto.getIconId())
                .orElseThrow(() -> new BusinessException("Icône non trouvée", HttpStatus.NOT_FOUND));
        route.setIcon(icon);

        List<RouteLocation> links = routeLocationRepository.findByIdRouteId(id);
        for (RouteLocation link : links) {
            Location loc = link.getLocation();
            if (STARTING.equals(link.getType())) {
                updateLocationFields(loc, dto.getStartingAddress());
            } else if (ARRIVAL.equals(link.getType())) {
                updateLocationFields(loc, dto.getArrivalAddress());
            }
            locationRepository.save(loc);
        }

        return routeRepository.save(route);
    }

    // Méthode utilitaire privée pour éviter la répétition
    private void updateLocationFields(Location loc, RouteRequest.AddressRequest addrDto) {
        loc.setStreetNumber(addrDto.getStreetNumber());
        loc.setStreetName(addrDto.getStreetName());
        loc.setPostalCode(addrDto.getPostalCode());
        loc.setCityName(addrDto.getCity());
        loc.setLatitude(addrDto.getLatitude());
        loc.setLongitude(addrDto.getLongitude());
    }
}