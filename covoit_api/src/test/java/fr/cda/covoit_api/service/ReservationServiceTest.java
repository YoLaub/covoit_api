package fr.cda.covoit_api.service;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.ProfilRepository;
import fr.cda.covoit_api.repository.RouteRepository;
import fr.cda.covoit_api.repository.UserRouteRepository;
import fr.cda.covoit_api.service.impl.ReservationServiceImpl;
import fr.cda.covoit_api.service.interfaces.IEmailService;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private UserRouteRepository userRouteRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private ProfilRepository profilRepository;
    @Mock
    private IEmailService emailService;
    @Mock
    private IRouteService routeService;
    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Route route;
    private Profil driver;
    private Profil passenger;
    private User driverAccount;

    @BeforeEach
    void setUp() {
        // --- Driver ---
        driverAccount = new User();
        driverAccount.setEmail("driver@test.com");

        driver = new Profil();
        driver.setId(1);
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");
        driver.setUser(driverAccount);

        // --- Passenger ---
        User passengerAccount = new User();
        passengerAccount.setEmail("passenger@test.com");

        passenger = new Profil();
        passenger.setId(2);
        passenger.setFirstname("Marie");
        passenger.setLastname("Martin");
        passenger.setUser(passengerAccount);

        // --- Route avec 2 places ---
        route = new Route();
        route.setId(1);
        route.setPlace((short) 2);
        route.setDate(LocalDate.now().plusDays(1));
        route.setHour(LocalTime.of(8, 0));
        route.setDistance(465);
        route.setDriver(driver);
    }

    // ============================================================
    // TEST 1 : Trajet complet → BusinessException
    // Réf: ReservationServiceImpl.java ligne ~64
    // ============================================================
    @Test
    void reservePlace_ShouldThrowException_WhenRouteIsFull() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(profilRepository.findByUserEmail("passenger@test.com")).thenReturn(Optional.of(passenger));
        when(userRouteRepository.findById(any(UserRouteId.class))).thenReturn(Optional.empty());
        when(userRouteRepository.countByRouteIdAndStatusNot(1, "cancelled")).thenReturn(2L);

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.reservePlace(1, "passenger@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Plus de places disponibles");
    }

    // ============================================================
    // TEST 2 : Trajet non trouvé → BusinessException NOT_FOUND
    // Réf: ReservationServiceImpl.java ligne ~42
    // ============================================================
    @Test
    void reservePlace_ShouldThrowException_WhenRouteNotFound() {
        // GIVEN
        when(routeRepository.findById(999)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.reservePlace(999, "passenger@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Trajet non trouvé");
    }

    // ============================================================
    // TEST 3 : Le conducteur ne peut pas réserver son propre trajet
    // Réf: ReservationServiceImpl.java ligne ~49
    // ============================================================
    @Test
    void reservePlace_ShouldThrowException_WhenDriverReservesOwnRoute() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(profilRepository.findByUserEmail("driver@test.com")).thenReturn(Optional.of(driver));

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.reservePlace(1, "driver@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("conducteur ne peut pas réserver son propre trajet");
    }

    // ============================================================
    // TEST 4 : Réservation en doublon → BusinessException CONFLICT
    // Réf: ReservationServiceImpl.java ligne ~54
    // ============================================================
    @Test
    void reservePlace_ShouldThrowException_WhenAlreadyReserved() {
        // GIVEN
        UserRoute existingReservation = new UserRoute();
        existingReservation.setStatus("confirmed");

        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(profilRepository.findByUserEmail("passenger@test.com")).thenReturn(Optional.of(passenger));
        when(userRouteRepository.findById(any(UserRouteId.class))).thenReturn(Optional.of(existingReservation));

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.reservePlace(1, "passenger@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà réservé");
    }

    // ============================================================
    // TEST 5 : Annulation d'une réservation déjà annulée → BusinessException
    // Réf: ReservationServiceImpl.java ligne ~88
    // ============================================================
    @Test
    void cancelReservation_ShouldThrowException_WhenAlreadyCancelled() {
        // GIVEN
        UserRoute cancelledReservation = new UserRoute();
        cancelledReservation.setStatus("cancelled");
        cancelledReservation.setRoute(route);

        when(profilRepository.findByUserEmail("passenger@test.com")).thenReturn(Optional.of(passenger));
        when(userRouteRepository.findById(any(UserRouteId.class))).thenReturn(Optional.of(cancelledReservation));

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.cancelReservation(1, "passenger@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà annulée");
    }
}