package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RouteRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Route testRoute;

    @BeforeEach
    void setUp() {
        // --- Icon (requis par Route, NOT NULL) ---
        Icon icon = new Icon();
        icon.setLabel("default-icon");
        entityManager.persist(icon);

        // --- Status et Role (requis par UserAccount) ---
        Status status = new Status();
        status.setLabel("active");
        entityManager.persist(status);

        Role role = new Role();
        role.setLabel("ROLE_USER");
        entityManager.persist(role);

        // --- UserAccount (requis par Profil) ---
        User account = new User();
        account.setEmail("driver@test.com");
        account.setPassword("hashed_password");
        account.setStatus(status);
        account.setRole(role);
        entityManager.persist(account);

        // --- Profil conducteur (requis par Route.driver) ---
        Profil driver = new Profil();
        driver.setFirstname("Jean");
        driver.setLastname("Dupont");
        driver.setPhone("0612345678");
        driver.setUser(account);
        entityManager.persist(driver);

        // --- Location départ : Paris ---
        Location paris = new Location();
        paris.setStreetName("Rue de Rivoli");
        paris.setPostalCode("75001");
        paris.setCityName("Paris");
        paris.setLatitude(48.85661400);
        paris.setLongitude(2.35222190);
        entityManager.persist(paris);

        // --- Location arrivée : Lyon ---
        Location lyon = new Location();
        lyon.setStreetName("Place Bellecour");
        lyon.setPostalCode("69002");
        lyon.setCityName("Lyon");
        lyon.setLatitude(45.75780100);
        lyon.setLongitude(4.83201100);
        entityManager.persist(lyon);

        // --- Route ---
        testRoute = new Route();
        testRoute.setPlace((short) 3);
        testRoute.setDate(LocalDate.now());
        testRoute.setHour(LocalTime.of(8, 0));
        testRoute.setDistance(465);
        testRoute.setIcon(icon);
        testRoute.setDriver(driver);
        entityManager.persist(testRoute);

        // --- RouteLocation départ ---
        RouteLocation rlStart = new RouteLocation();
        rlStart.setId(new RouteLocationId(testRoute.getId(), paris.getId()));
        rlStart.setRoute(testRoute);
        rlStart.setLocation(paris);
        rlStart.setType("starting");
        entityManager.persist(rlStart);

        // --- RouteLocation arrivée ---
        RouteLocation rlEnd = new RouteLocation();
        rlEnd.setId(new RouteLocationId(testRoute.getId(), lyon.getId()));
        rlEnd.setRoute(testRoute);
        rlEnd.setLocation(lyon);
        rlEnd.setType("arrival");
        entityManager.persist(rlEnd);

        entityManager.flush();
    }

    @Test
    void findBySearchCriteria_ShouldReturnMatchingRoutes() {
        // WHEN : Recherche Paris → Lyon à la date du jour
        List<Route> results = routeRepository.findBySearchCriteria("Paris", "Lyon", LocalDate.now());

        // THEN
        assertThat(results)
                .isNotEmpty()
                .hasSize(1)
                .first()
                .extracting(Route::getId)
                .isEqualTo(testRoute.getId());
    }

    @Test
    void findBySearchCriteria_WithWrongCity_ShouldReturnEmpty() {
        // WHEN : Recherche avec une ville qui n'existe pas
        List<Route> results = routeRepository.findBySearchCriteria("Marseille", "Lyon", LocalDate.now());

        // THEN
        assertThat(results).isEmpty();
    }

    @Test
    void findBySearchCriteria_WithNullDate_ShouldIgnoreDateFilter() {
        // WHEN : Recherche sans filtre de date
        List<Route> results = routeRepository.findBySearchCriteria("Paris", "Lyon", null);

        // THEN
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySearchCriteria_CaseInsensitive_ShouldWork() {
        // WHEN : Recherche en minuscules
        List<Route> results = routeRepository.findBySearchCriteria("paris", "lyon", LocalDate.now());

        // THEN : La query utilise LOWER(), donc ça doit matcher
        assertThat(results).isNotEmpty();
    }
}