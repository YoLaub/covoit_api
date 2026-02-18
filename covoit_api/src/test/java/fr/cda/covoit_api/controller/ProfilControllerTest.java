package fr.cda.covoit_api.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfilControllerTest {

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
        User user = new User();
        user.setEmail("user@test.com");

        profil = new Profil();
        profil.setId(1);
        profil.setFirstname("Jean");
        profil.setLastname("Dupont");
        profil.setUser(user);

        profilResponse = new ProfilResponse();
        profilResponse.setId(1);
        profilResponse.setFirstname("Jean");
        profilResponse.setLastname("Dupont");
    }

    // ============================================================
    // POST /api/persons
    // Réf: ProfilController.java → create()
    // ============================================================
    @Test
    @WithMockUser(username = "user@test.com")
    void create_ShouldReturn201() throws Exception {
        when(profilService.createProfil(any(), eq("user@test.com"))).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(post("/api/persons")
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
    // PATCH /api/persons/{id}
    // Réf: ProfilController.java → update()
    // ============================================================
    @Test
    @WithMockUser(username = "user@test.com")
    void update_ShouldReturn200() throws Exception {
        when(profilService.updateProfil(eq(1), any(), eq("user@test.com"))).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(patch("/api/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Pierre\",\"lastname\":\"Martin\",\"phone\":\"0698765432\"}"))
                .andExpect(status().isOk());
    }

    // ============================================================
    // DELETE /api/persons/{id}
    // Réf: ProfilController.java → delete()
    // ============================================================
    @Test
    @WithMockUser(username = "user@test.com")
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());

        verify(profilService).deleteProfil(1, "user@test.com");
    }

    // ============================================================
    // GET /api/persons/{id}/trips-driver
    // Réf: ProfilController.java → getTripsAsDriver()
    // ============================================================
    @Test
    @WithMockUser
    void getTripsAsDriver_ShouldReturn200() throws Exception {
        when(profilService.getDriverTrips(1)).thenReturn(List.of(new RouteResponse()));

        mockMvc.perform(get("/api/persons/1/trips-driver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================================
    // GET /api/persons/{id}/trips-passenger
    // Réf: ProfilController.java → getTripsAsPassenger()
    // ============================================================
    @Test
    @WithMockUser
    void getTripsAsPassenger_ShouldReturn200() throws Exception {
        when(profilService.getPassengerTrips(1)).thenReturn(List.of(new RouteResponse()));

        mockMvc.perform(get("/api/persons/1/trips-passenger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================================
    // GET /api/persons
    // Réf: ProfilController.java → getAll()
    // ============================================================
    @Test
    @WithMockUser
    void getAll_ShouldReturn200() throws Exception {
        when(profilService.getAllProfils()).thenReturn(List.of(profil));
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================================
    // GET /api/persons/{id}
    // Réf: ProfilController.java → getById()
    // ============================================================
    @Test
    @WithMockUser
    void getById_ShouldReturn200() throws Exception {
        when(profilService.getProfilById(1)).thenReturn(profil);
        when(entityMapper.toProfilResponse(profil)).thenReturn(profilResponse);

        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk());
    }
}