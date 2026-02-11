package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl  implements IRouteService {

    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final RouteLocationRepository routeLocationRepository;
    private final ProfilRepository profilRepository;

    @Transactional
    public Route createRoute(Route route, Location start, Location end, String email) {
        // 1. Récupérer le conducteur
        Profil driver = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil introuvable"));
        route.setDriver(driver);

        // 2. Sauvegarder les localisations
        Location savedStart = locationRepository.save(start);
        Location savedEnd = locationRepository.save(end);

        // 3. Sauvegarder le trajet
        Route savedRoute = routeRepository.save(route);

        // 4. Lier le départ
        RouteLocation startLink = new RouteLocation(
                new RouteLocationId(savedRoute.getId(), savedStart.getId()),
                savedRoute, savedStart, "starting"
        );
        routeLocationRepository.save(startLink);

        // 5. Lier l'arrivée
        RouteLocation endLink = new RouteLocation(
                new RouteLocationId(savedRoute.getId(), savedEnd.getId()),
                savedRoute, savedEnd, "arrival"
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
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Trajet introuvable"));
    }

    @Override
    @Transactional
    public void deleteRoute(Integer id, String email) {
        // Vérification que l'utilisateur est bien le driver (via email)
        // Logique de notification (Phase 7) et suppression cascade
        routeRepository.deleteById(id);
    }
}
