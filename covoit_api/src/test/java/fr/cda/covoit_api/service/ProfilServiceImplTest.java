package fr.cda.covoit_api.service;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.ProfilRequest;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.*;
import fr.cda.covoit_api.service.impl.ProfilServiceImpl;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilServiceImplTest {

    @Mock
    private ProfilRepository profilRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private UserRouteRepository userRouteRepository;
    @Mock
    private IRouteService routeService;
    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private ProfilServiceImpl profilService;

    private User user;
    private Profil profil;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@test.com");

        profil = new Profil();
        profil.setId(1);
        profil.setFirstname("Jean");
        profil.setLastname("Dupont");
        profil.setPhone("0612345678");
        profil.setUser(user);

        vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setSeats((short) 5);
        vehicle.setCarregistration("AB-123-CD");
        vehicle.setOwner(profil);
    }

    // ============================================================
    // createProfil
    // Réf: ProfilServiceImpl.java → createProfil()
    // ============================================================
    @Test
    void createProfil_ShouldSaveAndReturnProfil() {
        ProfilRequest dto = new ProfilRequest();
        dto.setFirstname("Jean");
        dto.setLastname("Dupont");
        dto.setPhone("0612345678");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(profilRepository.save(any(Profil.class))).thenAnswer(i -> i.getArgument(0));

        Profil result = profilService.createProfil(dto, "user@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getFirstname()).isEqualTo("Jean");
        verify(profilRepository).save(any(Profil.class));
    }

    @Test
    void createProfil_ShouldThrow_WhenUserNotFound() {
        ProfilRequest dto = new ProfilRequest();
        String email = "unknown@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.createProfil(dto, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Compte non trouvé");
    }

    // ============================================================
    // addVehicle
    // Réf: ProfilServiceImpl.java → addVehicle()
    // ============================================================
    @Test
    void addVehicle_ShouldSaveVehicle() {
        when(vehicleRepository.existsByOwnerId(1)).thenReturn(false);
        when(profilRepository.findById(1)).thenReturn(Optional.of(profil));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle newVehicle = new Vehicle();
        newVehicle.setSeats((short) 5);

        Vehicle result = profilService.addVehicle(newVehicle, 1);

        assertThat(result).isNotNull();
        assertThat(result.getOwner()).isEqualTo(profil);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void addVehicle_ShouldThrow_WhenUserAlreadyHasVehicle() {
        Vehicle newVehicle = new Vehicle();
        Integer profilId = 1;
        when(vehicleRepository.existsByOwnerId(profilId)).thenReturn(true);

        assertThatThrownBy(() -> profilService.addVehicle(newVehicle, profilId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà un véhicule");
    }

    @Test
    void addVehicle_ShouldThrow_WhenProfilNotFound() {
        Vehicle newVehicle = new Vehicle();
        Integer profilId = 999;
        when(vehicleRepository.existsByOwnerId(profilId)).thenReturn(false);
        when(profilRepository.findById(profilId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.addVehicle(newVehicle, profilId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    // ============================================================
    // getProfilByEmail
    // Réf: ProfilServiceImpl.java → getProfilByEmail()
    // ============================================================
    @Test
    void getProfilByEmail_ShouldReturnProfil() {
        when(profilRepository.findByUserEmail("user@test.com")).thenReturn(Optional.of(profil));

        Profil result = profilService.getProfilByEmail("user@test.com");

        assertThat(result).isEqualTo(profil);
    }

    @Test
    void getProfilByEmail_ShouldThrow_WhenNotFound() {
        String email = "unknown@test.com";
        when(profilRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getProfilByEmail(email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    // ============================================================
    // updateProfil
    // Réf: ProfilServiceImpl.java → updateProfil()
    // ============================================================
    @Test
    void updateProfil_ShouldUpdateFields() {
        ProfilRequest dto = new ProfilRequest();
        dto.setFirstname("Pierre");
        dto.setLastname("Martin");
        dto.setPhone("0698765432");

        when(profilRepository.findById(1)).thenReturn(Optional.of(profil));
        when(profilRepository.save(any(Profil.class))).thenAnswer(i -> i.getArgument(0));

        Profil result = profilService.updateProfil(1, dto, "user@test.com");

        assertThat(result.getFirstname()).isEqualTo("Pierre");
        assertThat(result.getLastname()).isEqualTo("Martin");
        assertThat(result.getPhone()).isEqualTo("0698765432");
    }

    @Test
    void updateProfil_ShouldThrow_WhenNotOwner() {
        ProfilRequest dto = new ProfilRequest();
        Integer id = 1;
        String email = "other@test.com";
        when(profilRepository.findById(id)).thenReturn(Optional.of(profil));

        assertThatThrownBy(() -> profilService.updateProfil(id, dto, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Action non autorisée");
    }

    @Test
    void updateProfil_ShouldThrow_WhenProfilNotFound() {
        ProfilRequest dto = new ProfilRequest();
        Integer id = 999;
        String email = "user@test.com";
        when(profilRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.updateProfil(id, dto, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    // ============================================================
    // deleteProfil
    // Réf: ProfilServiceImpl.java → deleteProfil()
    // ============================================================
    @Test
    void deleteProfil_ShouldDeleteUser() {
        when(profilRepository.findById(1)).thenReturn(Optional.of(profil));

        profilService.deleteProfil(1, "user@test.com");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteProfil_ShouldThrow_WhenNotOwner() {
        Integer id = 1;
        String email = "other@test.com";
        when(profilRepository.findById(id)).thenReturn(Optional.of(profil));

        assertThatThrownBy(() -> profilService.deleteProfil(id, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Non autorisé");
    }

    // ============================================================
    // getVehicleByEmail
    // Réf: ProfilServiceImpl.java → getVehicleByEmail()
    // ============================================================
    @Test
    void getVehicleByEmail_ShouldReturnVehicle() {
        when(vehicleRepository.findByOwnerUserEmail("user@test.com")).thenReturn(Optional.of(vehicle));

        Vehicle result = profilService.getVehicleByEmail("user@test.com");

        assertThat(result).isEqualTo(vehicle);
    }

    @Test
    void getVehicleByEmail_ShouldThrow_WhenNotFound() {
        String email = "user@test.com";
        when(vehicleRepository.findByOwnerUserEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getVehicleByEmail(email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Aucun véhicule trouvé");
    }

    // ============================================================
    // updateVehicle
    // Réf: ProfilServiceImpl.java → updateVehicle()
    // ============================================================
    @Test
    void updateVehicle_ShouldUpdateFields() {
        Vehicle details = new Vehicle();
        details.setSeats((short) 4);
        details.setCarregistration("XY-999-ZZ");
        details.setAdditionalInfo("GPS");
        details.setModel(new Model());

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle result = profilService.updateVehicle(1, details, "user@test.com");

        assertThat(result.getSeats()).isEqualTo((short) 4);
        assertThat(result.getCarregistration()).isEqualTo("XY-999-ZZ");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_ShouldThrow_WhenNotOwner() {
        Vehicle details = new Vehicle();
        Integer id = 1;
        String email = "other@test.com";
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> profilService.updateVehicle(id, details, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pas autorisé");
    }

    @Test
    void updateVehicle_ShouldThrow_WhenVehicleNotFound() {
        Vehicle details = new Vehicle();
        Integer id = 999;
        String email = "user@test.com";
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.updateVehicle(id, details, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Véhicule introuvable");
    }

    // ============================================================
    // deleteVehicle
    // Réf: ProfilServiceImpl.java → deleteVehicle()
    // ============================================================
    @Test
    void deleteVehicle_ShouldDelete() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        profilService.deleteVehicle(1, "user@test.com");

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_ShouldThrow_WhenNotOwner() {
        Integer id = 1;
        String email = "other@test.com";
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> profilService.deleteVehicle(id, email))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Action interdite");
    }

    // ============================================================
    // getDriverTrips
    // Réf: ProfilServiceImpl.java → getDriverTrips()
    // ============================================================
    @Test
    void getDriverTrips_ShouldReturnRouteResponses() {
        Route route = new Route();
        route.setId(1);
        Location paris = new Location();
        Location lyon = new Location();
        RouteResponse response = new RouteResponse();

        when(profilRepository.existsById(1)).thenReturn(true);
        when(routeRepository.findByDriverId(1)).thenReturn(List.of(route));
        when(routeService.getLocationsForRoute(1)).thenReturn(Map.of("starting", paris, "arrival", lyon));
        when(entityMapper.toRouteResponse(route, paris, lyon)).thenReturn(response);

        List<RouteResponse> results = profilService.getDriverTrips(1);

        assertThat(results).hasSize(1);
    }

    @Test
    void getDriverTrips_ShouldThrow_WhenProfilNotFound() {
        Integer profilId = 999;
        when(profilRepository.existsById(profilId)).thenReturn(false);

        assertThatThrownBy(() -> profilService.getDriverTrips(profilId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    // ============================================================
    // getPassengerTrips
    // Réf: ProfilServiceImpl.java → getPassengerTrips()
    // ============================================================
    @Test
    void getPassengerTrips_ShouldReturnRouteResponses() {
        Route route = new Route();
        route.setId(1);
        Location paris = new Location();
        Location lyon = new Location();
        RouteResponse response = new RouteResponse();

        UserRoute ur = new UserRoute();
        ur.setRoute(route);

        when(profilRepository.existsById(1)).thenReturn(true);
        when(userRouteRepository.findByPassengerId(1)).thenReturn(List.of(ur));
        when(routeService.getLocationsForRoute(1)).thenReturn(Map.of("starting", paris, "arrival", lyon));
        when(entityMapper.toRouteResponse(route, paris, lyon)).thenReturn(response);

        List<RouteResponse> results = profilService.getPassengerTrips(1);

        assertThat(results).hasSize(1);
    }

    @Test
    void getPassengerTrips_ShouldThrow_WhenProfilNotFound() {
        Integer profilId = 999;
        when(profilRepository.existsById(profilId)).thenReturn(false);

        assertThatThrownBy(() -> profilService.getPassengerTrips(profilId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    // ============================================================
    // getAllProfils / getProfilById / getAllVehicles / getVehicleById
    // ============================================================
    @Test
    void getAllProfils_ShouldReturnList() {
        when(profilRepository.findAll()).thenReturn(List.of(profil));

        List<Profil> results = profilService.getAllProfils();

        assertThat(results).hasSize(1);
    }

    @Test
    void getProfilById_ShouldReturnProfil() {
        when(profilRepository.findById(1)).thenReturn(Optional.of(profil));

        Profil result = profilService.getProfilById(1);

        assertThat(result).isEqualTo(profil);
    }

    @Test
    void getProfilById_ShouldThrow_WhenNotFound() {
        Integer id = 999;
        when(profilRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getProfilById(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Profil non trouvé");
    }

    @Test
    void getAllVehicles_ShouldReturnList() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<Vehicle> results = profilService.getAllVehicles();

        assertThat(results).hasSize(1);
    }

    @Test
    void getVehicleById_ShouldReturnVehicle() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        Vehicle result = profilService.getVehicleById(1);

        assertThat(result).isEqualTo(vehicle);
    }

    @Test
    void getVehicleById_ShouldThrow_WhenNotFound() {
        Integer id = 999;
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profilService.getVehicleById(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Véhicule non trouvé");
    }
}