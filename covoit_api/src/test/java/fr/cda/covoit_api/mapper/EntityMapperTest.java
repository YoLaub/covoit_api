package fr.cda.covoit_api.mapper;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.RouteRequest;
import fr.cda.covoit_api.dto.request.VehicleRequest;
import fr.cda.covoit_api.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTest {

    private EntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EntityMapper();
    }

    // ============================================================
    // toProfilResponse
    // ============================================================
    @Test
    void toProfilResponse_ShouldMapAllFields() {
        User account = new User();
        account.setEmail("jean@test.com");

        Profil profil = new Profil();
        profil.setId(1);
        profil.setFirstname("Jean");
        profil.setLastname("Dupont");
        profil.setPhone("0612345678");
        profil.setUser(account);

        ProfilResponse result = mapper.toProfilResponse(profil);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstname()).isEqualTo("Jean");
        assertThat(result.getLastname()).isEqualTo("Dupont");
        assertThat(result.getPhone()).isEqualTo("0612345678");
        assertThat(result.getEmail()).isEqualTo("jean@test.com");
    }

    @Test
    void toProfilResponse_ShouldReturnNull_WhenProfilIsNull() {
        assertThat(mapper.toProfilResponse(null)).isNull();
    }

    // ============================================================
    // toLocationResponse
    // ============================================================
    @Test
    void toLocationResponse_ShouldMapAllFields() {
        Location loc = new Location();
        loc.setStreetNumber("10");
        loc.setStreetName("Rue de Rivoli");
        loc.setPostalCode("75001");
        loc.setCityName("Paris");

        LocationResponse result = mapper.toLocationResponse(loc);

        assertThat(result).isNotNull();
        assertThat(result.getStreetNumber()).isEqualTo("10");
        assertThat(result.getStreetName()).isEqualTo("Rue de Rivoli");
        assertThat(result.getPostalCode()).isEqualTo("75001");
        assertThat(result.getCityName()).isEqualTo("Paris");
    }

    @Test
    void toLocationResponse_ShouldReturnNull_WhenLocationIsNull() {
        assertThat(mapper.toLocationResponse(null)).isNull();
    }

    // ============================================================
    // toRouteResponse
    // ============================================================
    @Test
    void toRouteResponse_ShouldMapAllFields() {
        Icon icon = new Icon();
        icon.setLabel("car-icon");

        Profil driver = new Profil();
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");

        Route route = new Route();
        route.setId(1);
        route.setDistance(465);
        route.setPlace((short) 3);
        route.setDate(LocalDate.of(2026, 3, 15));
        route.setHour(LocalTime.of(8, 0));
        route.setIcon(icon);
        route.setDriver(driver);

        Location start = new Location();
        start.setCityName("Paris");
        Location end = new Location();
        end.setCityName("Lyon");

        RouteResponse result = mapper.toRouteResponse(route, start, end);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getKms()).isEqualTo(465);
        assertThat(result.getAvailableSeats()).isEqualTo((short) 3);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(result.getHour()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.getIconLabel()).isEqualTo("car-icon");
        assertThat(result.getDriverName()).isEqualTo("Jean Dupont");
        assertThat(result.getDeparture()).isNotNull();
        assertThat(result.getArrival()).isNotNull();
    }

    @Test
    void toRouteResponse_ShouldHandleNullIcon() {
        Profil driver = new Profil();
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");

        Route route = new Route();
        route.setId(1);
        route.setDistance(465);
        route.setPlace((short) 3);
        route.setDate(LocalDate.now());
        route.setHour(LocalTime.of(8, 0));
        route.setIcon(null);
        route.setDriver(driver);

        RouteResponse result = mapper.toRouteResponse(route, new Location(), new Location());

        assertThat(result).isNotNull();
        assertThat(result.getIconLabel()).isNull();
    }

    @Test
    void toRouteResponse_ShouldReturnNull_WhenRouteIsNull() {
        assertThat(mapper.toRouteResponse(null, new Location(), new Location())).isNull();
    }

    // ============================================================
    // toLocation
    // ============================================================
    @Test
    void toLocation_ShouldMapAllFields() {
        RouteRequest.AddressRequest addr = new RouteRequest.AddressRequest();
        addr.setStreetNumber("10");
        addr.setStreetName("Rue Neuve");
        addr.setPostalCode("75002");
        addr.setCity("Paris");
        addr.setLatitude(48.8566);
        addr.setLongitude(2.3522);

        Location result = mapper.toLocation(addr);

        assertThat(result).isNotNull();
        assertThat(result.getStreetNumber()).isEqualTo("10");
        assertThat(result.getStreetName()).isEqualTo("Rue Neuve");
        assertThat(result.getPostalCode()).isEqualTo("75002");
        assertThat(result.getCityName()).isEqualTo("Paris");
        assertThat(result.getLatitude()).isEqualTo(48.8566);
        assertThat(result.getLongitude()).isEqualTo(2.3522);
    }

    @Test
    void toLocation_ShouldReturnNull_WhenAddressIsNull() {
        assertThat(mapper.toLocation(null)).isNull();
    }

    // ============================================================
    // toRoute
    // ============================================================
    @Test
    void toRoute_ShouldMapAllFields() {
        RouteRequest dto = new RouteRequest();
        dto.setAvailableSeats((short) 3);
        dto.setTripDate(LocalDate.of(2026, 3, 15));
        dto.setTripHour(LocalTime.of(8, 0));
        dto.setKms(465);

        Route result = mapper.toRoute(dto);

        assertThat(result).isNotNull();
        assertThat(result.getPlace()).isEqualTo((short) 3);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(result.getHour()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.getDistance()).isEqualTo(465);
    }

    @Test
    void toRoute_ShouldReturnNull_WhenDtoIsNull() {
        assertThat(mapper.toRoute(null)).isNull();
    }

    // ============================================================
    // toVehicle
    // ============================================================
    @Test
    void toVehicle_ShouldMapAllFields() {
        Model model = new Model();
        model.setLabel("Clio");

        VehicleRequest dto = new VehicleRequest();
        dto.setSeats((short) 5);
        dto.setCarregistration("AB-123-CD");
        dto.setAdditionalInfo("Climatisation");

        Vehicle result = mapper.toVehicle(dto, model);

        assertThat(result).isNotNull();
        assertThat(result.getSeats()).isEqualTo((short) 5);
        assertThat(result.getCarregistration()).isEqualTo("AB-123-CD");
        assertThat(result.getAdditionalInfo()).isEqualTo("Climatisation");
        assertThat(result.getModel().getLabel()).isEqualTo("Clio");
    }

    @Test
    void toVehicle_ShouldReturnNull_WhenDtoIsNull() {
        assertThat(mapper.toVehicle(null, new Model())).isNull();
    }

    // ============================================================
    // toVehicleResponse
    // ============================================================
    @Test
    void toVehicleResponse_ShouldMapAllFields() {
        Brand brand = new Brand();
        brand.setLabel("Renault");

        Model model = new Model();
        model.setLabel("Clio");
        model.setBrand(brand);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setSeats((short) 5);
        vehicle.setCarregistration("AB-123-CD");
        vehicle.setAdditionalInfo("Climatisation");
        vehicle.setModel(model);

        VehicleResponse result = mapper.toVehicleResponse(vehicle);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getSeats()).isEqualTo((short) 5);
        assertThat(result.getCarregistration()).isEqualTo("AB-123-CD");
        assertThat(result.getAdditionalInfo()).isEqualTo("Climatisation");
        assertThat(result.getModelName()).isEqualTo("Clio");
        assertThat(result.getBrandName()).isEqualTo("Renault");
    }

    @Test
    void toVehicleResponse_ShouldHandleNullModel() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setModel(null);

        VehicleResponse result = mapper.toVehicleResponse(vehicle);

        assertThat(result).isNotNull();
        assertThat(result.getModelName()).isNull();
        assertThat(result.getBrandName()).isNull();
    }

    @Test
    void toVehicleResponse_ShouldHandleNullBrand() {
        Model model = new Model();
        model.setLabel("Clio");
        model.setBrand(null);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setModel(model);

        VehicleResponse result = mapper.toVehicleResponse(vehicle);

        assertThat(result).isNotNull();
        assertThat(result.getModelName()).isEqualTo("Clio");
        assertThat(result.getBrandName()).isNull();
    }

    @Test
    void toVehicleResponse_ShouldReturnNull_WhenVehicleIsNull() {
        assertThat(mapper.toVehicleResponse(null)).isNull();
    }

    // ============================================================
    // toBrandResponse
    // ============================================================
    @Test
    void toBrandResponse_ShouldMapAllFields() {
        Brand brand = new Brand();
        brand.setId(1);
        brand.setLabel("Renault");

        BrandResponse result = mapper.toBrandResponse(brand);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getLabel()).isEqualTo("Renault");
    }

    @Test
    void toBrandResponse_ShouldReturnNull_WhenBrandIsNull() {
        assertThat(mapper.toBrandResponse(null)).isNull();
    }

    // ============================================================
    // toReservationResponse
    // ============================================================
    @Test
    void toReservationResponse_ShouldMapAllFields() {
        Profil driver = new Profil();
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");

        Route route = new Route();
        route.setId(1);
        route.setDate(LocalDate.of(2026, 3, 15));
        route.setHour(LocalTime.of(8, 0));
        route.setDriver(driver);

        UserRoute ur = new UserRoute();
        ur.setRoute(route);
        ur.setStatus("confirmed");
        ur.setCreatedAt(LocalDateTime.of(2026, 3, 10, 14, 0));

        Location start = new Location();
        start.setCityName("Paris");
        Location end = new Location();
        end.setCityName("Lyon");

        ReservationResponse result = mapper.toReservationResponse(ur, start, end);

        assertThat(result).isNotNull();
        assertThat(result.getRouteId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo("confirmed");
        assertThat(result.getDriverName()).isEqualTo("Jean Dupont");
        assertThat(result.getDepartureCity()).isEqualTo("Paris");
        assertThat(result.getArrivalCity()).isEqualTo("Lyon");
    }

    @Test
    void toReservationResponse_ShouldHandleNullLocations() {
        Profil driver = new Profil();
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");

        Route route = new Route();
        route.setId(1);
        route.setDate(LocalDate.now());
        route.setHour(LocalTime.of(8, 0));
        route.setDriver(driver);

        UserRoute ur = new UserRoute();
        ur.setRoute(route);
        ur.setStatus("pending");
        ur.setCreatedAt(LocalDateTime.now());

        ReservationResponse result = mapper.toReservationResponse(ur, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getDepartureCity()).isNull();
        assertThat(result.getArrivalCity()).isNull();
    }

    @Test
    void toReservationResponse_ShouldReturnNull_WhenUserRouteIsNull() {
        assertThat(mapper.toReservationResponse(null, new Location(), new Location())).isNull();
    }
}