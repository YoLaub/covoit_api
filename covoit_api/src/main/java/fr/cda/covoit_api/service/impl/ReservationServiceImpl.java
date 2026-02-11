package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.interfaces.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final UserRouteRepository userRouteRepository;
    private final RouteRepository routeRepository;
    private final ProfilRepository profilRepository;

    @Override
    @Transactional
    public UserRoute reservePlace(Integer routeId, String passengerEmail) {
        // 1. Récupérer le trajet
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        // 2. Récupérer le profil du passager via son email (token)
        Profil passenger = profilRepository.findByUserEmail(passengerEmail)
                .orElseThrow(() -> new RuntimeException("Profil passager non trouvé"));

        // 3. Règle métier : On ne peut pas réserver son propre trajet
        if (route.getDriver().getId().equals(passenger.getId())) {
            throw new RuntimeException("Un conducteur ne peut pas réserver son propre trajet");
        }

        // 4. Règle métier : Vérifier la disponibilité des places
        long currentPassengers = userRouteRepository.countByRouteIdAndStatusNot(routeId, "cancelled");
        if (currentPassengers >= route.getPlace()) {
            throw new RuntimeException("Plus de places disponibles pour ce trajet");
        }

        // 5. Créer la réservation
        UserRoute reservation = new UserRoute();
        reservation.setId(new UserRouteId(passenger.getId(), route.getId()));
        reservation.setPassenger(passenger);
        reservation.setRoute(route);
        reservation.setStatus("confirmed"); // Par défaut confirmé pour ce MVP

        return userRouteRepository.save(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Integer routeId, String passengerEmail) {
        Profil passenger = profilRepository.findByUserEmail(passengerEmail)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));

        UserRouteId id = new UserRouteId(passenger.getId(), routeId);
        UserRoute reservation = userRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatus("cancelled");
        userRouteRepository.save(reservation);
    }

    @Override
    public List<UserRoute> getPassengerReservations(String email) {
        Profil passenger = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
        return userRouteRepository.findByPassengerId(passenger.getId());
    }
}