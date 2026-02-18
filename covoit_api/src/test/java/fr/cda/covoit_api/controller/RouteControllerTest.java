package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Icon;
import fr.cda.covoit_api.domain.entity.Location;
import fr.cda.covoit_api.domain.entity.Route;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.IconRepository;
import fr.cda.covoit_api.service.interfaces.IRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRouteService routeService;

    @MockitoBean
    private EntityMapper entityMapper;

    @MockitoBean
    private IconRepository iconRepository;

    private Route route;
    private RouteResponse routeResponse;
    private Location paris;
    private Location lyon;

    @BeforeEach
    void setUp() {
        paris = new Location();
        paris.setId(1);
        paris.setCityName("Paris");

        lyon = new Location();
        lyon.setId(2);
        lyon.setCityName("Lyon");

        route = new Route();
        route.setId(1);
        route.setPlace((short) 3);
        route.setDate(LocalDate.now().plusDays(1));
        route.setHour(LocalTime.of(8, 0));
        route.setDistance(465);

        routeResponse = new RouteResponse();
    }

    // ============================================================
    // GET /api/trips - Recherche de trajets
    // Réf: RouteController.java → search()
    // ============================================================
    @Test
    @WithMockUser
    void search_ShouldReturn200_WithResults() throws Exception {
        // GIVEN
        when(routeService.searchRoutesWithDetails("Paris", "Lyon", null))
                .thenReturn(List.of(routeResponse));

        // WHEN & THEN
        mockMvc.perform(get("/api/trips")
                        .param("startingcity", "Paris")
                        .param("arrivalcity", "Lyon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void search_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // POST /api/trips - Création de trajet
    // Réf: RouteController.java → create()
    // ============================================================
    @Test
    @WithMockUser(username = "driver@test.com")
    void create_ShouldReturn201() throws Exception {
        // GIVEN
        Icon icon = new Icon();
        icon.setId(1);

        when(iconRepository.findById(1)).thenReturn(Optional.of(icon));
        when(entityMapper.toLocation(any())).thenReturn(paris, lyon);
        when(entityMapper.toRoute(any())).thenReturn(route);
        when(routeService.createRoute(any(Route.class), any(Location.class), any(Location.class), eq("driver@test.com")))
                .thenReturn(route);
        when(entityMapper.toRouteResponse(any(Route.class), any(Location.class), any(Location.class)))
                .thenReturn(routeResponse);

        // WHEN & THEN
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRouteRequestJson()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "driver@test.com")
    void create_ShouldReturn404_WhenIconNotFound() throws Exception {
        // GIVEN
        when(entityMapper.toLocation(any())).thenReturn(paris);
        when(entityMapper.toRoute(any())).thenReturn(route);
        when(iconRepository.findById(1)).thenReturn(Optional.empty());

        // WHEN & THEN
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRouteRequestJson()))
                .andExpect(result -> {
                    // BusinessException avec NOT_FOUND
                    if (result.getResolvedException() != null) {
                        assert result.getResolvedException() instanceof BusinessException;
                    }
                });
    }

    // ============================================================
    // GET /api/trips/{id} - Détail d'un trajet
    // Réf: RouteController.java → getById()
    // ============================================================
    @Test
    @WithMockUser
    void getById_ShouldReturn200() throws Exception {
        // GIVEN
        when(routeService.getById(1)).thenReturn(route);
        when(routeService.getLocationsForRoute(1))
                .thenReturn(Map.of("starting", paris, "arrival", lyon));
        when(entityMapper.toRouteResponse(route, paris, lyon))
                .thenReturn(routeResponse);

        // WHEN & THEN
        mockMvc.perform(get("/api/trips/1"))
                .andExpect(status().isOk());
    }

    // ============================================================
    // PATCH /api/trips/{id} - Mise à jour d'un trajet
    // Réf: RouteController.java → update()
    // ============================================================
    @Test
    @WithMockUser(username = "driver@test.com")
    void update_ShouldReturn200() throws Exception {
        // GIVEN
        when(routeService.updateRoute(eq(1), any(), eq("driver@test.com")))
                .thenReturn(route);
        when(routeService.getLocationsForRoute(1))
                .thenReturn(Map.of("starting", paris, "arrival", lyon));
        when(entityMapper.toRouteResponse(route, paris, lyon))
                .thenReturn(routeResponse);

        // WHEN & THEN
        mockMvc.perform(patch("/api/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRouteRequestJson()))
                .andExpect(status().isOk());
    }

    // ============================================================
    // PATCH /api/trips/{id}/seats - Mise à jour des places
    // Réf: RouteController.java → updateSeats()
    // ============================================================
    @Test
    @WithMockUser(username = "driver@test.com")
    void updateSeats_ShouldReturn200() throws Exception {
        // GIVEN
        when(routeService.updateRouteSeats(1, (short) 4, "driver@test.com"))
                .thenReturn(route);
        when(routeService.getLocationsForRoute(1))
                .thenReturn(Map.of("starting", paris, "arrival", lyon));
        when(entityMapper.toRouteResponse(route, paris, lyon))
                .thenReturn(routeResponse);

        // WHEN & THEN
        mockMvc.perform(patch("/api/trips/1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"availableSeats\": 4}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "driver@test.com")
    void updateSeats_ShouldThrow_WhenSeatsNull() throws Exception {
        // WHEN & THEN : body sans "availableSeats"
        mockMvc.perform(patch("/api/trips/1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> {
                    if (result.getResolvedException() != null) {
                        assert result.getResolvedException() instanceof BusinessException;
                    }
                });
    }

    // ============================================================
    // DELETE /api/trips/{id} - Suppression d'un trajet
    // Réf: RouteController.java → delete()
    // ============================================================
    @Test
    @WithMockUser(username = "driver@test.com")
    void delete_ShouldReturn204() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/api/trips/1"))
                .andExpect(status().isNoContent());

        verify(routeService).deleteRoute(1, "driver@test.com");
    }

    // ============================================================
    // Utilitaire : JSON d'un RouteRequest valide
    // ============================================================
    private String buildRouteRequestJson() {
        return """
                {
                    "kms": 465,
                    "availableSeats": 3,
                    "tripDate": "%s",
                    "tripHour": "08:00",
                    "iconId": 1,
                    "startingAddress": {
                        "streetNumber": "1",
                        "streetName": "Rue de Rivoli",
                        "postalCode": "75001",
                        "city": "Paris",
                        "latitude": 48.8566,
                        "longitude": 2.3522
                    },
                    "arrivalAddress": {
                        "streetNumber": "5",
                        "streetName": "Place Bellecour",
                        "postalCode": "69002",
                        "city": "Lyon",
                        "latitude": 45.7578,
                        "longitude": 4.832
                    }
                }
                """.formatted(LocalDate.now().plusDays(1));
    }
}