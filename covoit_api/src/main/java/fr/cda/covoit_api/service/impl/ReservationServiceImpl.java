package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.interfaces.IEmailService;
import fr.cda.covoit_api.service.interfaces.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.cda.covoit_api.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final UserRouteRepository userRouteRepository;
    private final RouteRepository routeRepository;
    private final ProfilRepository profilRepository;
    private final IEmailService emailService;
    private static final String STATUS_CANCELLED = "cancelled";
    private static final String STATUS_CONFIRMED = "confirmed";

    @Override
    @Transactional
    public UserRoute reservePlace(Integer routeId, String passengerEmail) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new BusinessException("Trajet non trouvé", HttpStatus.NOT_FOUND));

        Profil passenger = profilRepository.findByUserEmail(passengerEmail)
                .orElseThrow(() -> new BusinessException("Profil passager non trouvé", HttpStatus.NOT_FOUND));

        // Règle métier : Un conducteur ne peut pas réserver son propre trajet
        if (route.getDriver().getId().equals(passenger.getId())) {
            throw new BusinessException("Un conducteur ne peut pas réserver son propre trajet", HttpStatus.BAD_REQUEST);
        }

        // Règle métier : Vérifier qu'il n'a pas déjà réservé
        UserRouteId reservationId = new UserRouteId(passenger.getId(), route.getId());
        userRouteRepository.findById(reservationId).ifPresent(existing -> {
            if (!STATUS_CANCELLED.equals(existing.getStatus())) {
                throw new BusinessException("Vous avez déjà réservé ce trajet", HttpStatus.CONFLICT);
            }
        });

        // Règle métier : Vérifier la disponibilité des places
        long currentPassengers = userRouteRepository.countByRouteIdAndStatusNot(routeId, "STATUS_CANCELLED");
        if (currentPassengers >= route.getPlace()) {
            throw new BusinessException("Plus de places disponibles pour ce trajet", HttpStatus.CONFLICT);
        }

        // Créer la réservation
        UserRoute reservation = new UserRoute();
        reservation.setId(reservationId);
        reservation.setPassenger(passenger);
        reservation.setRoute(route);
        reservation.setStatus(STATUS_CONFIRMED);

        UserRoute saved = userRouteRepository.save(reservation);

        // Notification au conducteur
        emailService.sendSimpleMessage(
                route.getDriver().getUser().getEmail(),
                "Nouvelle réservation",
                "Le passager " + passenger.getFirstname() + " a réservé une place sur votre trajet."
        );

        return saved;
    }

    @Override
    @Transactional
    public void cancelReservation(Integer routeId, String passengerEmail) {
        Profil passenger = profilRepository.findByUserEmail(passengerEmail)
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));

        UserRouteId id = new UserRouteId(passenger.getId(), routeId);
        UserRoute reservation = userRouteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Réservation non trouvée", HttpStatus.NOT_FOUND));

        if ("STATUS_CANCELLED".equals(reservation.getStatus())) {
            throw new BusinessException("Cette réservation est déjà annulée", HttpStatus.BAD_REQUEST);
        }

        reservation.setStatus(STATUS_CANCELLED);
        userRouteRepository.save(reservation);

        // Notification au conducteur
        emailService.sendSimpleMessage(
                reservation.getRoute().getDriver().getUser().getEmail(),
                "Annulation de réservation",
                "Le passager " + passenger.getFirstname() + " a annulé sa réservation sur votre trajet."
        );
    }

    @Override
    public List<UserRoute> getPassengerReservations(String email) {
        Profil passenger = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));

        return userRouteRepository.findByPassengerIdAndStatusNot(passenger.getId(), STATUS_CANCELLED);
    }
}