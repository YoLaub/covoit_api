package fr.cda.covoit_api.service;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.impl.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private RouteLocationRepository routeLocationRepository;
    @Mock
    private ProfilRepository profilRepository;
    @Mock
    private IconRepository iconRepository;
    @Mock
    private EntityMapper entityMapper;
    @Mock
    private UserRouteRepository userRouteRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route route;
    private Profil driver;
    private User driverAccount;
    private Location paris;
    private Location lyon;
    private Icon icon;

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

        // --- Icon ---
        icon = new Icon();
        icon.setId(1);
        icon.setLabel("car-icon");

        // --- Locations ---
        paris = new Location();
        paris.setId(1);
        paris.setCityName("Paris");
        paris.setStreetName("Rue de Rivoli");
        paris.setPostalCode("75001");

        lyon = new Location();
        lyon.setId(2);
        lyon.setCityName("Lyon");
        lyon.setStreetName("Place Bellecour");
        lyon.setPostalCode("69002");

        // --- Route ---
        route = new Route();
        route.setId(1);
        route.setPlace((short) 3);
        route.setDate(LocalDate.now().plusDays(1));
        route.setHour(LocalTime.of(8, 0));
        route.setDistance(465);
        route.setIcon(icon);
        route.setDriver(driver);
    }

    // ============================================================
    // createRoute
    // Réf: RouteServiceImpl.java → createRoute()
    // ============================================================
    @Test
    void createRoute_ShouldSaveRouteAndLocations() {
        // GIVEN
        Route newRoute = new Route();
        newRoute.setPlace((short) 3);
        newRoute.setDate(LocalDate.now().plusDays(1));
        newRoute.setHour(LocalTime.of(8, 0));
        newRoute.setDistance(465);
        newRoute.setIcon(icon);

        when(profilRepository.findByUserEmail("driver@test.com")).thenReturn(Optional.of(driver));
        when(locationRepository.save(paris)).thenReturn(paris);
        when(locationRepository.save(lyon)).thenReturn(lyon);
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> {
            Route r = invocation.getArgument(0);
            r.setId(1);
            return r;
        });
        when(routeLocationRepository.save(any(RouteLocation.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Route result = routeService.createRoute(newRoute, paris, lyon, "driver@test.com");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getDriver()).isEqualTo(driver);
        verify(profilRepository).findByUserEmail("driver@test.com");
        verify(locationRepository, times(2)).save(any(Location.class));
        verify(routeRepository).save(any(Route.class));
        verify(routeLocationRepository, times(2)).save(any(RouteLocation.class));
    }

    @Test
    void createRoute_ShouldThrowException_WhenProfilNotFound() {
        // GIVEN
        when(profilRepository.findByUserEmail("unknown@test.com")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.createRoute(new Route(), paris, lyon, "unknown@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil introuvable");
    }

    // ============================================================
    // searchRoutes
    // Réf: RouteServiceImpl.java → searchRoutes()
    // ============================================================
    @Test
    void searchRoutes_ShouldReturnMatchingRoutes() {
        // GIVEN
        when(routeRepository.findBySearchCriteria("Paris", "Lyon", LocalDate.now()))
                .thenReturn(List.of(route));

        // WHEN
        List<Route> results = routeService.searchRoutes("Paris", "Lyon", LocalDate.now());

        // THEN
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1);
    }

    // ============================================================
    // searchRoutesWithDetails
    // Réf: RouteServiceImpl.java → searchRoutesWithDetails()
    // ============================================================
    @Test
    void searchRoutesWithDetails_ShouldReturnRouteResponses() {
        // GIVEN
        RouteResponse mockResponse = new RouteResponse();
        RouteLocation rlStart = new RouteLocation(
                new RouteLocationId(1, 1), route, paris, "starting"
        );
        RouteLocation rlEnd = new RouteLocation(
                new RouteLocationId(1, 2), route, lyon, "arrival"
        );

        when(routeRepository.findBySearchCriteria("Paris", "Lyon", null))
                .thenReturn(List.of(route));
        when(routeLocationRepository.findByIdRouteId(1))
                .thenReturn(List.of(rlStart, rlEnd));
        when(entityMapper.toRouteResponse(route, paris, lyon))
                .thenReturn(mockResponse);

        // WHEN
        List<RouteResponse> results = routeService.searchRoutesWithDetails("Paris", "Lyon", null);

        // THEN
        assertThat(results).hasSize(1);
        verify(entityMapper).toRouteResponse(route, paris, lyon);
    }

    // ============================================================
    // getById
    // Réf: RouteServiceImpl.java → getById()
    // ============================================================
    @Test
    void getById_ShouldReturnRoute() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));

        // WHEN
        Route result = routeService.getById(1);

        // THEN
        assertThat(result).isEqualTo(route);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // GIVEN
        when(routeRepository.findById(999)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.getById(999))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Trajet introuvable");
    }

    // ============================================================
    // getLocationsForRoute
    // Réf: RouteServiceImpl.java → getLocationsForRoute()
    // ============================================================
    @Test
    void getLocationsForRoute_ShouldReturnStartAndArrival() {
        // GIVEN
        RouteLocation rlStart = new RouteLocation(
                new RouteLocationId(1, 1), route, paris, "starting"
        );
        RouteLocation rlEnd = new RouteLocation(
                new RouteLocationId(1, 2), route, lyon, "arrival"
        );
        when(routeLocationRepository.findByIdRouteId(1)).thenReturn(List.of(rlStart, rlEnd));

        // WHEN
        var locations = routeService.getLocationsForRoute(1);

        // THEN
        assertThat(locations).containsKey("starting");
        assertThat(locations).containsKey("arrival");
        assertThat(locations.get("starting").getCityName()).isEqualTo("Paris");
        assertThat(locations.get("arrival").getCityName()).isEqualTo("Lyon");
    }

    // ============================================================
    // deleteRoute
    // Réf: RouteServiceImpl.java → deleteRoute()
    // ============================================================
    @Test
    void deleteRoute_ShouldDelete_WhenDriverIsOwner() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));

        // WHEN
        routeService.deleteRoute(1, "driver@test.com");

        // THEN
        verify(routeRepository).deleteById(1);
    }

    @Test
    void deleteRoute_ShouldThrowException_WhenNotOwner() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.deleteRoute(1, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pas le conducteur");
    }

    @Test
    void deleteRoute_ShouldThrowException_WhenRouteNotFound() {
        // GIVEN
        when(routeRepository.findById(999)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.deleteRoute(999, "driver@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Trajet introuvable");
    }

    // ============================================================
    // updateRouteSeats
    // Réf: RouteServiceImpl.java → updateRouteSeats()
    // ============================================================
    @Test
    void updateRouteSeats_ShouldUpdateCapacity() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(userRouteRepository.countByRouteIdAndStatusNot(1, "cancelled")).thenReturn(1L);
        when(routeRepository.save(route)).thenReturn(route);

        // WHEN
        Route result = routeService.updateRouteSeats(1, (short) 4, "driver@test.com");

        // THEN
        assertThat(result.getPlace()).isEqualTo((short) 4);
        verify(routeRepository).save(route);
    }

    @Test
    void updateRouteSeats_ShouldThrowException_WhenNotOwner() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.updateRouteSeats(1, (short) 4, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Seul le conducteur");
    }

    @Test
    void updateRouteSeats_ShouldThrowException_WhenCapacityBelowReservations() {
        // GIVEN
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(userRouteRepository.countByRouteIdAndStatusNot(1, "cancelled")).thenReturn(3L);

        // WHEN & THEN : on essaie de descendre à 2 places alors qu'il y a 3 réservations
        assertThatThrownBy(() -> routeService.updateRouteSeats(1, (short) 2, "driver@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("réduire la capacité");
    }

    // ============================================================
    // updateRoute
    // Réf: RouteServiceImpl.java → updateRoute()
    // ============================================================
    @Test
    void updateRoute_ShouldUpdateAllFields() {
        // GIVEN
        RouteRequest dto = buildRouteRequest();

        RouteLocation rlStart = new RouteLocation(
                new RouteLocationId(1, 1), route, paris, "starting"
        );
        RouteLocation rlEnd = new RouteLocation(
                new RouteLocationId(1, 2), route, lyon, "arrival"
        );

        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(userRouteRepository.countByRouteIdAndStatusNot(1, "cancelled")).thenReturn(0L);
        when(iconRepository.findById(2)).thenReturn(Optional.of(icon));
        when(routeLocationRepository.findByIdRouteId(1)).thenReturn(List.of(rlStart, rlEnd));
        when(locationRepository.save(any(Location.class))).thenAnswer(i -> i.getArgument(0));
        when(routeRepository.save(route)).thenReturn(route);

        // WHEN
        Route result = routeService.updateRoute(1, dto, "driver@test.com");

        // THEN
        assertThat(result.getDistance()).isEqualTo(500);
        assertThat(result.getPlace()).isEqualTo((short) 4);
        verify(locationRepository, times(2)).save(any(Location.class));
        verify(routeRepository).save(route);
    }

    @Test
    void updateRoute_ShouldThrowException_WhenNotOwner() {
        // GIVEN
        RouteRequest dto = buildRouteRequest();
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.updateRoute(1, dto, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Action non autorisée");
    }

    @Test
    void updateRoute_ShouldThrowException_WhenIconNotFound() {
        // GIVEN
        RouteRequest dto = buildRouteRequest();
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(userRouteRepository.countByRouteIdAndStatusNot(1, "cancelled")).thenReturn(0L);
        when(iconRepository.findById(2)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> routeService.updateRoute(1, dto, "driver@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Icône non trouvée");
    }

    // ============================================================
    // Méthode utilitaire pour construire un RouteRequest de test
    // ============================================================
    private RouteRequest buildRouteRequest() {
        RouteRequest dto = new RouteRequest();
        dto.setKms(500);
        dto.setAvailableSeats((short) 4);
        dto.setTripDate(LocalDate.now().plusDays(2));
        dto.setTripHour(LocalTime.of(10, 0));
        dto.setIconId(2);

        RouteRequest.AddressRequest startAddr = new RouteRequest.AddressRequest();
        startAddr.setStreetNumber("10");
        startAddr.setStreetName("Rue Neuve");
        startAddr.setPostalCode("75002");
        startAddr.setCity("Paris");
        startAddr.setLatitude(48.8566);
        startAddr.setLongitude(2.3522);
        dto.setStartingAddress(startAddr);

        RouteRequest.AddressRequest endAddr = new RouteRequest.AddressRequest();
        endAddr.setStreetNumber("5");
        endAddr.setStreetName("Avenue Foch");
        endAddr.setPostalCode("69001");
        endAddr.setCity("Lyon");
        endAddr.setLatitude(45.7578);
        endAddr.setLongitude(4.8320);
        dto.setArrivalAddress(endAddr);

        return dto;
    }
}