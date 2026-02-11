package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.dto.request.RegisterRequest;
import fr.cda.covoit_api.dto.response.AuthResponse;
import fr.cda.covoit_api.repository.RoleRepository;
import fr.cda.covoit_api.repository.StatusRepository;
import fr.cda.covoit_api.repository.UserRepository;
import fr.cda.covoit_api.security.JwtTokenProvider;
import fr.cda.covoit_api.service.interfaces.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           StatusRepository statusRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.statusRepository = statusRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Désormais opérationnel grâce aux repositories créés
        user.setRole(roleRepository.findByLabel("USER")
                .orElseThrow(() -> new RuntimeException("Rôle par défaut non configuré")));
        user.setStatus(statusRepository.findByLabel("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Statut par défaut non configuré")));

        userRepository.save(user);

        // Génération du token pour connecter l'utilisateur immédiatement après inscription
        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }

    @Override
    public AuthResponse login(String email, String password) {
        // 1. Authentification via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 2. Récupération de l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 3. Génération du token
        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }
}