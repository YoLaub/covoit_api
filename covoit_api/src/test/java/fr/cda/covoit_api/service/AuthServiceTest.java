package fr.cda.covoit_api.service;

import fr.cda.covoit_api.domain.entity.Role;
import fr.cda.covoit_api.domain.entity.Status;
import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.dto.request.RegisterRequest;
import fr.cda.covoit_api.dto.response.AuthResponse;
import fr.cda.covoit_api.repository.RoleRepository;
import fr.cda.covoit_api.repository.StatusRepository;
import fr.cda.covoit_api.repository.UserRepository;
import fr.cda.covoit_api.security.JwtTokenProvider;
import fr.cda.covoit_api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role roleUser;
    private Status activeStatus;

    @BeforeEach
    void setUp() {
        roleUser = new Role();
        roleUser.setId(1);
        roleUser.setLabel("USER");

        activeStatus = new Status();
        activeStatus.setId(1);
        activeStatus.setLabel("ACTIVE");
    }

    // ============================================================
    // register - Inscription réussie
    // Réf: AuthServiceImpl.java → register()
    // ============================================================
    @Test
    void register_ShouldReturnAuthResponse_WhenEmailNotTaken() {
        // GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(roleRepository.findByLabel("USER")).thenReturn(Optional.of(roleUser));
        when(statusRepository.findByLabel("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(tokenProvider.generateToken("new@test.com")).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        AuthResponse response = authService.register(request);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getEmail()).isEqualTo("new@test.com");
        assertThat(response.getRole()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    // ============================================================
    // register - Email déjà utilisé
    // Réf: AuthServiceImpl.java → register() ligne "Email déjà utilisé"
    // ============================================================
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email déjà utilisé");

        verify(userRepository, never()).save(any());
    }

    // ============================================================
    // register - Rôle par défaut non configuré
    // Réf: AuthServiceImpl.java → register() ligne "Rôle par défaut non configuré"
    // ============================================================
    @Test
    void register_ShouldThrowException_WhenDefaultRoleNotFound() {
        // GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(roleRepository.findByLabel("USER")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rôle par défaut non configuré");
    }

    // ============================================================
    // register - Statut par défaut non configuré
    // Réf: AuthServiceImpl.java → register() ligne "Statut par défaut non configuré"
    // ============================================================
    @Test
    void register_ShouldThrowException_WhenDefaultStatusNotFound() {
        // GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(roleRepository.findByLabel("USER")).thenReturn(Optional.of(roleUser));
        when(statusRepository.findByLabel("ACTIVE")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Statut par défaut non configuré");
    }

    // ============================================================
    // login - Connexion réussie
    // Réf: AuthServiceImpl.java → login()
    // ============================================================
    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() {
        // GIVEN
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("hashed_password");
        user.setRole(roleUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user@test.com", null));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken("user@test.com")).thenReturn("jwt-token-456");

        // WHEN
        AuthResponse response = authService.login("user@test.com", "password123");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-456");
        assertThat(response.getEmail()).isEqualTo("user@test.com");
        assertThat(response.getRole()).isEqualTo("USER");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // ============================================================
    // login - Mauvais credentials
    // Réf: AuthServiceImpl.java → login() via authenticationManager
    // ============================================================
    @Test
    void login_ShouldThrowException_WhenBadCredentials() {
        // GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // WHEN & THEN
        assertThatThrownBy(() -> authService.login("user@test.com", "wrong_password"))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ============================================================
    // login - Utilisateur non trouvé après authentification
    // Réf: AuthServiceImpl.java → login() ligne "Utilisateur non trouvé"
    // ============================================================
    @Test
    void login_ShouldThrowException_WhenUserNotFoundAfterAuth() {
        // GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("ghost@test.com", null));
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> authService.login("ghost@test.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }
}