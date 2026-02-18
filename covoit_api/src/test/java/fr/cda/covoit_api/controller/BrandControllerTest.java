package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.AbstractIntegrationTest;
import fr.cda.covoit_api.domain.entity.Brand;
import fr.cda.covoit_api.dto.response.BrandResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.BrandRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandRepository brandRepository;

    @MockitoBean
    private EntityMapper entityMapper;

    private Brand brand;
    private BrandResponse brandResponse;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1);
        brand.setLabel("Renault");

        brandResponse = new BrandResponse(1, "Renault");
    }

    // ============================================================
    // GET /api/brands - Accessible par tous les utilisateurs authentifiés
    // Réf: BrandController.java → getAll()
    // ============================================================
    @Test
    @WithMockUser
    void getAll_ShouldReturn200() throws Exception {
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(entityMapper.toBrandResponse(brand)).thenReturn(brandResponse);

        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].label").value("Renault"));
    }

    @Test
    void getAll_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // POST /api/brands - Réservé ADMIN
    // Réf: SecurityConfig.java → .requestMatchers(HttpMethod.POST, "/api/brands/**").hasRole("ADMIN")
    // ============================================================
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_WithAdmin_ShouldReturn201() throws Exception {
        when(brandRepository.findByLabel("Peugeot")).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(entityMapper.toBrandResponse(brand)).thenReturn(brandResponse);

        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Peugeot\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void create_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Peugeot\"}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // PUT /api/brands/{id} - Réservé ADMIN
    // Réf: SecurityConfig.java → .requestMatchers(HttpMethod.PUT, "/api/brands/**").hasRole("ADMIN")
    // ============================================================
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void update_WithAdmin_ShouldReturn200() throws Exception {
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(entityMapper.toBrandResponse(brand)).thenReturn(brandResponse);

        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Renault Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void update_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Renault Updated\"}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // DELETE /api/brands/{id} - Réservé ADMIN
    // Réf: SecurityConfig.java → .requestMatchers(HttpMethod.DELETE, "/api/brands/**").hasRole("ADMIN")
    // ============================================================
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_WithAdmin_ShouldReturn204() throws Exception {
        when(brandRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/brands/1"))
                .andExpect(status().isNoContent());

        verify(brandRepository).deleteById(1);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void delete_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/brands/1"))
                .andExpect(status().isForbidden());
    }
}