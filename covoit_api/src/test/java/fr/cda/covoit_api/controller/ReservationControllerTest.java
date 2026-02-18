package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.AbstractIntegrationTest;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.ReservationResponse;
import fr.cda.covoit_api.service.interfaces.IReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReservationService reservationService;

    // ============================================================
    // POST /api/trips/{id}/person - Réserver une place
    // Réf: ReservationController.java → reserve()
    // ============================================================
    @Test
    @WithMockUser(username = "passenger@test.com")
    void reserve_ShouldReturn200() throws Exception {
        // GIVEN
        ReservationResponse response = new ReservationResponse();
        when(reservationService.reservePlace(1, "passenger@test.com"))
                .thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/trips/1/person"))
                .andExpect(status().isOk());

        verify(reservationService).reservePlace(1, "passenger@test.com");
    }

    @Test
    void reserve_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/trips/1/person"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/trips/my-reservations - Mes réservations
    // Réf: ReservationController.java → getMyReservations()
    // ============================================================
    @Test
    @WithMockUser(username = "passenger@test.com")
    void getMyReservations_ShouldReturn200() throws Exception {
        // GIVEN
        when(reservationService.getPassengerReservations("passenger@test.com"))
                .thenReturn(List.of(new ReservationResponse()));

        // WHEN & THEN
        mockMvc.perform(get("/api/trips/my-reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "passenger@test.com")
    void getMyReservations_ShouldReturnEmptyList_WhenNoReservations() throws Exception {
        // GIVEN
        when(reservationService.getPassengerReservations("passenger@test.com"))
                .thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/trips/my-reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ============================================================
    // DELETE /api/trips/{id}/person - Annuler une réservation
    // Réf: ReservationController.java → cancel()
    // ============================================================
    @Test
    @WithMockUser(username = "passenger@test.com")
    void cancel_ShouldReturn204() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/api/trips/1/person"))
                .andExpect(status().isNoContent());

        verify(reservationService).cancelReservation(1, "passenger@test.com");
    }

    @Test
    void cancel_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/trips/1/person"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/trips/{id}/person - Passagers d'un trajet
    // Réf: ReservationController.java → getRoutePassengers()
    // ============================================================
    @Test
    @WithMockUser
    void getRoutePassengers_ShouldReturn200() throws Exception {
        // GIVEN
        when(reservationService.getPassengersByRouteId(1))
                .thenReturn(List.of(new ProfilResponse()));

        // WHEN & THEN
        mockMvc.perform(get("/api/trips/1/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void getRoutePassengers_ShouldReturnEmptyList_WhenNoPassengers() throws Exception {
        // GIVEN
        when(reservationService.getPassengersByRouteId(1))
                .thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/trips/1/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}