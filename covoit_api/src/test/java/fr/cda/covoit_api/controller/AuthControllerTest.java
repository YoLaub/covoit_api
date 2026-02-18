package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.dto.response.AuthResponse;
import fr.cda.covoit_api.service.interfaces.IAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuthService authService;

    // ============================================================
    // POST /register - Routes publiques (pas besoin de @WithMockUser)
    // Réf: AuthController.java → register()
    // ============================================================
    @Test
    void register_ShouldReturn200() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .token("jwt-token-123")
                .email("new@test.com")
                .role("USER")
                .build();

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ============================================================
    // POST /login
    // Réf: AuthController.java → login()
    // ============================================================
    @Test
    void login_ShouldReturn200() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .token("jwt-token-456")
                .email("user@test.com")
                .role("USER")
                .build();

        when(authService.login("user@test.com", "password123")).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-456"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }
}