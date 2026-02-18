package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.AbstractIntegrationTest;
import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfilControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProfilService profilService;

    @MockitoBean
    private EntityMapper entityMapper;

    private Profil profil;
    private ProfilResponse profilResponse;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setEmail("user@test.com");

        profil = new Profil();
        profil.setId(1);
        profil.setFirstname("Jean");
        profil.setLastname("Dupont");
        profil.setUser(u);

        profilResponse = new ProfilResponse();
        profilResponse.setId(1);
        profilResponse.setFirstname("Jean");
        profilResponse.setLastname("Dupont");
    }

    // ============================================================
    // POST /api/persons → authenticated()
    // ============================================================
    @Test
    void create_ShouldReturn201() throws Exception {
        when(profilService.createProfil(any(), eq("user@test.com"))).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(post("/api/persons")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Dupont\",\"phone\":\"0612345678\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void create_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jean\",\"lastname\":\"Dupont\",\"phone\":\"0612345678\"}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // PATCH /api/persons/{id} → authenticated()
    // ============================================================
    @Test
    void update_ShouldReturn200() throws Exception {
        when(profilService.updateProfil(eq(1), any(), eq("user@test.com"))).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(patch("/api/persons/1")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Pierre\",\"lastname\":\"Martin\",\"phone\":\"0698765432\"}"))
                .andExpect(status().isOk());
    }

    // ============================================================
    // DELETE /api/persons/{id} → hasRole(ADMIN)
    // ============================================================
    @Test
    void delete_WithAdmin_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/persons/1")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(profilService).deleteProfil(1, "admin@test.com");
    }

    @Test
    void delete_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/persons/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/persons/{id}/trips-driver → authenticated()
    // ============================================================
    @Test
    void getTripsAsDriver_ShouldReturn200() throws Exception {
        when(profilService.getDriverTrips(1)).thenReturn(List.of(new RouteResponse()));

        mockMvc.perform(get("/api/persons/1/trips-driver")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================================
    // GET /api/persons/{id}/trips-passenger → authenticated()
    // ============================================================
    @Test
    void getTripsAsPassenger_ShouldReturn200() throws Exception {
        when(profilService.getPassengerTrips(1)).thenReturn(List.of(new RouteResponse()));

        mockMvc.perform(get("/api/persons/1/trips-passenger")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================================
    // GET /api/persons → hasRole(ADMIN)
    // ============================================================
    @Test
    void getAll_WithAdmin_ShouldReturn200() throws Exception {
        when(profilService.getAllProfils()).thenReturn(List.of(profil));
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(get("/api/persons")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAll_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/persons")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/persons/{id} → authenticated()
    // ============================================================
    @Test
    void getById_ShouldReturn200() throws Exception {
        when(profilService.getProfilById(1)).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(get("/api/persons/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk());
    }
}