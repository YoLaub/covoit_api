package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.AbstractIntegrationTest;
import fr.cda.covoit_api.domain.entity.Brand;
import fr.cda.covoit_api.domain.entity.Model;
import fr.cda.covoit_api.dto.response.ModelResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.BrandRepository;
import fr.cda.covoit_api.repository.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelRepository modelRepository;

    @MockitoBean
    private BrandRepository brandRepository;

    @MockitoBean
    private EntityMapper entityMapper;

    private Brand brand;
    private Model model;
    private ModelResponse modelResponse;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1);
        brand.setLabel("Renault");

        model = new Model();
        model.setId(1);
        model.setLabel("Clio");
        model.setBrand(brand);

        modelResponse = new ModelResponse(1, "Clio", 1, "Renault");
    }

    // ============================================================
    // GET /api/models → authenticated()
    // ============================================================
    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(modelRepository.findAll()).thenReturn(List.of(model));
        when(entityMapper.toModelResponse(model)).thenReturn(modelResponse);

        mockMvc.perform(get("/api/models")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].label").value("Clio"));
    }

    @Test
    void getAll_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/models"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/models/brand/{brandId} → authenticated()
    // ============================================================
    @Test
    void getByBrand_ShouldReturn200() throws Exception {
        when(modelRepository.findByBrandId(1)).thenReturn(List.of(model));
        when(entityMapper.toModelResponse(model)).thenReturn(modelResponse);

        mockMvc.perform(get("/api/models/brand/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].brandLabel").value("Renault"));
    }

    // ============================================================
    // POST /api/models → hasRole(ADMIN)
    // ============================================================
    @Test
    void create_WithAdmin_ShouldReturn201() throws Exception {
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(modelRepository.save(any(Model.class))).thenReturn(model);
        when(entityMapper.toModelResponse(model)).thenReturn(modelResponse);

        mockMvc.perform(post("/api/models")
                        .with(user("admin@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Clio\",\"brandId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void create_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/models")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Clio\",\"brandId\":1}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // PUT /api/models/{id} → hasRole(ADMIN)
    // ============================================================
    @Test
    void update_WithAdmin_ShouldReturn200() throws Exception {
        when(modelRepository.findById(1)).thenReturn(Optional.of(model));
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(modelRepository.save(any(Model.class))).thenReturn(model);
        when(entityMapper.toModelResponse(model)).thenReturn(modelResponse);

        mockMvc.perform(put("/api/models/1")
                        .with(user("admin@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Clio Updated\",\"brandId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void update_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/models/1")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Clio Updated\",\"brandId\":1}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // DELETE /api/models/{id} → hasRole(ADMIN)
    // ============================================================
    @Test
    void delete_WithAdmin_ShouldReturn204() throws Exception {
        when(modelRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/models/1")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(modelRepository).deleteById(1);
    }

    @Test
    void delete_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/models/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }
}