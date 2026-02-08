package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.Role;
import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.dto.request.RegisterRequest;
import fr.cda.covoit_api.dto.response.AuthResponse;
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

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 1. Vérification unicité (Phase 2 Plan.md)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 2. Création de l'utilisateur
        User user = new User();
        user.setPseudo(request.getPseudo());
        user.setEmail(request.getEmail());
        // Hachage du mot de passe (Phase 2 Plan.md)
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEtat(true);

        // Note: Ici, on devrait récupérer le rôle "USER" depuis un RoleRepository
        // Pour l'exemple, nous supposons qu'il est déjà injecté ou géré

        userRepository.save(user);

        // 3. Génération du token
        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .pseudo(user.getPseudo())
                .email(user.getEmail())
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
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }
}