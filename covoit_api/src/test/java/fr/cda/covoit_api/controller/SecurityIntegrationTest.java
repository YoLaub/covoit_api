package fr.cda.covoit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ============================================================
    // TEST 1 : Accès sans authentification → 401 ou 403
    // Réf: SecurityConfig.java → .requestMatchers("/api/**").authenticated()
    // ============================================================
    @Test
    void accessTrips_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // TEST 2 : Route admin avec rôle USER → 403 Forbidden
    // Réf: SecurityConfig.java → .requestMatchers(HttpMethod.POST, "/api/brands/**").hasRole("ADMIN")
    // ============================================================
    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void adminRoute_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/brands")
                        .contentType("application/json")
                        .content("{\"label\": \"TestBrand\"}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // TEST 3 : Route admin avec rôle ADMIN → pas de 403
    // Réf: SecurityConfig.java → .requestMatchers(HttpMethod.POST, "/api/brands/**").hasRole("ADMIN")
    // ============================================================
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void adminRoute_WithAdminRole_ShouldNotReturn403() throws Exception {
        mockMvc.perform(post("/api/brands")
                        .contentType("application/json")
                        .content("{\"label\": \"TestBrand\"}"))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    // ============================================================
    // TEST 4 : Routes publiques accessibles sans auth
    // Réf: SecurityConfig.java → .requestMatchers("/register", "/login", "/health").permitAll()
    // ============================================================
    @Test
    void publicRoutes_WithoutAuth_ShouldNotReturn401Or403() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }
}