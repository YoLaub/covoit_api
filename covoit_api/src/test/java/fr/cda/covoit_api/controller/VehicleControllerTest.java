package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.AbstractIntegrationTest;
import fr.cda.covoit_api.domain.entity.Model;
import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.dto.response.VehicleResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.ModelRepository;
import fr.cda.covoit_api.repository.ProfilRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProfilService profilService;

    @MockitoBean
    private ProfilRepository profilRepository;

    @MockitoBean
    private ModelRepository modelRepository;

    @MockitoBean
    private EntityMapper entityMapper;

    private Vehicle vehicle;
    private VehicleResponse vehicleResponse;
    private Profil profil;
    private Model model;

    @BeforeEach
    void setUp() {
        profil = new Profil();
        profil.setId(1);

        model = new Model();
        model.setId(1);
        model.setLabel("Clio");

        vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setSeats((short) 5);
        vehicle.setCarregistration("AB-123-CD");

        vehicleResponse = new VehicleResponse();
        vehicleResponse.setId(1);
    }

    // ============================================================
    // POST /api/cars → authenticated()
    // ============================================================
    @Test
    void create_ShouldReturn201() throws Exception {
        when(profilRepository.findByUserEmail("user@test.com")).thenReturn(Optional.of(profil));
        when(modelRepository.findById(1)).thenReturn(Optional.of(model));
        when(entityMapper.toVehicle(any(), eq(model))).thenReturn(vehicle);
        when(profilService.addVehicle(vehicle, 1)).thenReturn(vehicle);
        when(entityMapper.toVehicleResponse(vehicle)).thenReturn(vehicleResponse);

        mockMvc.perform(post("/api/cars")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seats\":5,\"carregistration\":\"AB-123-CD\",\"modelId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void create_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seats\":5,\"carregistration\":\"AB-123-CD\",\"modelId\":1}"))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/cars/my-car → authenticated()
    // ============================================================
    @Test
    void getMyVehicle_ShouldReturn200() throws Exception {
        when(profilService.getVehicleByEmail("user@test.com")).thenReturn(vehicle);
        when(entityMapper.toVehicleResponse(vehicle)).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/cars/my-car")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk());
    }

    // ============================================================
    // PUT /api/cars/{id} → authenticated()
    // ============================================================
    @Test
    void update_ShouldReturn200() throws Exception {
        when(modelRepository.findById(1)).thenReturn(Optional.of(model));
        when(entityMapper.toVehicle(any(), eq(model))).thenReturn(vehicle);
        when(profilService.updateVehicle(eq(1), any(), eq("user@test.com"))).thenReturn(vehicle);
        when(entityMapper.toVehicleResponse(vehicle)).thenReturn(vehicleResponse);

        mockMvc.perform(put("/api/cars/1")
                        .with(user("user@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seats\":5,\"carregistration\":\"AB-123-CD\",\"modelId\":1}"))
                .andExpect(status().isOk());
    }

    // ============================================================
    // DELETE /api/cars/{id} → authenticated()
    // ============================================================
    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/cars/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isNoContent());

        verify(profilService).deleteVehicle(1, "user@test.com");
    }

    // ============================================================
    // GET /api/cars → hasRole(ADMIN)
    // ============================================================
    @Test
    void getAllCars_WithAdmin_ShouldReturn200() throws Exception {
        when(profilService.getAllVehicles()).thenReturn(List.of(vehicle));
        when(entityMapper.toVehicleResponse(vehicle)).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/cars")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllCars_WithUser_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/cars")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GET /api/cars/{id} → authenticated()
    // ============================================================
    @Test
    void getCarById_ShouldReturn200() throws Exception {
        when(profilService.getVehicleById(1)).thenReturn(vehicle);
        when(entityMapper.toVehicleResponse(vehicle)).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/cars/1")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk());
    }
}