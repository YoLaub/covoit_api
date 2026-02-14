package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.ProfilRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements IProfilService {
    private final ProfilRepository profilRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    private final RouteRepository routeRepository;
    private final UserRouteRepository userRouteRepository;
    private final IRouteService routeService;
    private final EntityMapper entityMapper;

    private static final String PROFIL_NOT_FOUND ="Profil non trouvé";

    @Override
    public Profil createProfil(ProfilRequest dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Compte non trouvé", HttpStatus.NOT_FOUND));

        Profil profil = new Profil();
        profil.setFirstname(dto.getFirstname());
        profil.setLastname(dto.getLastname());
        profil.setPhone(dto.getPhone());
        profil.setUser(user);

        return profilRepository.save(profil);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle, Integer profilId) {
        if (vehicleRepository.existsByOwnerId(profilId)) {
            throw new BusinessException("L'utilisateur possède déjà un véhicule.", HttpStatus.CONFLICT);
        }
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND));
        vehicle.setOwner(profil);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Profil getProfilByEmail(String email) {
        return profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public Profil updateProfil(Integer id, ProfilRequest dto, String emailRequestor) {
        Profil current = profilRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!current.getUser().getEmail().equals(emailRequestor)) {
            throw new BusinessException("Action non autorisée", HttpStatus.FORBIDDEN);
        }

        current.setFirstname(dto.getFirstname());
        current.setLastname(dto.getLastname());
        current.setPhone(dto.getPhone());

        return profilRepository.save(current);
    }

    @Transactional
    @Override
    public void deleteProfil(Integer id, String requestorEmail) {
        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Sécurité : Seul l'utilisateur ou un ADMIN peut supprimer
        if (!profil.getUser().getEmail().equals(requestorEmail)) {
            // Logique de vérification de rôle ADMIN pourrait être ajoutée ici
            throw new BusinessException("Non autorisé", HttpStatus.FORBIDDEN);
        }

        // Suppression du compte User (le cascade JPA s'occupera du Profil et du Véhicule)
        userRepository.delete(profil.getUser());
    }

    @Override
    public Vehicle getVehicleByEmail(String email) {
        return vehicleRepository.findByOwnerUserEmail(email)
                .orElseThrow(() -> new BusinessException("Aucun véhicule trouvé pour cet utilisateur", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Integer id, Vehicle details, String emailRequestor) {
        Vehicle current = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Véhicule introuvable", HttpStatus.NOT_FOUND));

        // Sécurité : Vérifier que le véhicule appartient bien à l'utilisateur qui fait la requête
        if (!current.getOwner().getUser().getEmail().equals(emailRequestor)) {
            throw new BusinessException("Vous n'êtes pas autorisé à modifier ce véhicule", HttpStatus.FORBIDDEN);
        }

        current.setSeats(details.getSeats());
        current.setCarregistration(details.getCarregistration());
        current.setAdditionalInfo(details.getAdditionalInfo());
        current.setModel(details.getModel());

        return vehicleRepository.save(current);
    }

    @Override
    @Transactional
    public void deleteVehicle(Integer id, String emailRequestor) {
        Vehicle current = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Véhicule introuvable", HttpStatus.NOT_FOUND));

        if (!current.getOwner().getUser().getEmail().equals(emailRequestor)) {
            throw new BusinessException("Action interdite", HttpStatus.FORBIDDEN);
        }

        vehicleRepository.delete(current);
    }

    @Override
    public List<RouteResponse> getDriverTrips(Integer profilId) {
        if (!profilRepository.existsById(profilId)) {
            throw new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<Route> routes = routeRepository.findByDriverId(profilId);

        return routes.stream().map(route -> {
            Map<String, Location> locs = routeService.getLocationsForRoute(route.getId());
            return entityMapper.toRouteResponse(route, locs.get("starting"), locs.get("arrival"));
        }).toList();
    }

    @Override
    public List<RouteResponse> getPassengerTrips(Integer profilId) {
        if (!profilRepository.existsById(profilId)) {
            throw new BusinessException(PROFIL_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // Récupère les associations UserRoute du passager
        List<UserRoute> reservations = userRouteRepository.findByPassengerId(profilId);

        return reservations.stream().map(res -> {
            Route route = res.getRoute();
            Map<String, Location> locs = routeService.getLocationsForRoute(route.getId());
            return entityMapper.toRouteResponse(route, locs.get("starting"), locs.get("arrival"));
        }).toList();
    }
}