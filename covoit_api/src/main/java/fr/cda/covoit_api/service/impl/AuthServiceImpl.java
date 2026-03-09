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

/**
 * Impl&eacute;mentation du service d'authentification.
 * <p>
 * Cette classe g&egrave;re les op&eacute;rations d'inscription et de connexion des utilisateurs,
 * incluant la validation des donn&eacute;es, le hachage des mots de passe,
 * l'attribution des r&ocirc;les et statuts par d&eacute;faut, ainsi que la g&eacute;n&eacute;ration
 * des jetons JWT.
 * </p>
 *
 * @author Yoann Laubert
 * @version 1.0
 * @see IAuthService
 * @see JwtTokenProvider
 * @see UserRepository
 */
@Service
public class AuthServiceImpl implements IAuthService {

    /** R&eacute;f&eacute;rentiel d'acc&egrave;s aux donn&eacute;es des utilisateurs. */
    private final UserRepository userRepository;

    /** Encodeur utilis&eacute; pour le hachage des mots de passe. */
    private final PasswordEncoder passwordEncoder;

    /** Fournisseur de jetons JWT. */
    private final JwtTokenProvider tokenProvider;

    /** Gestionnaire d'authentification Spring Security. */
    private final AuthenticationManager authenticationManager;

    /** R&eacute;f&eacute;rentiel d'acc&egrave;s aux donn&eacute;es des r&ocirc;les. */
    private final RoleRepository roleRepository;

    /** R&eacute;f&eacute;rentiel d'acc&egrave;s aux donn&eacute;es des statuts. */
    private final StatusRepository statusRepository;

    /**
     * Constructeur avec injection des d&eacute;pendances.
     *
     * @param userRepository        r&eacute;f&eacute;rentiel des utilisateurs
     * @param roleRepository        r&eacute;f&eacute;rentiel des r&ocirc;les
     * @param statusRepository      r&eacute;f&eacute;rentiel des statuts
     * @param passwordEncoder       encodeur de mots de passe
     * @param tokenProvider         fournisseur de jetons JWT
     * @param authenticationManager gestionnaire d'authentification Spring Security
     */
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

    /**
     * Inscrit un nouvel utilisateur dans le syst&egrave;me.
     * <p>
     * V&eacute;rifie que l'adresse e-mail n'est pas d&eacute;j&agrave; utilis&eacute;e,
     * cr&eacute;e le compte avec le r&ocirc;le {@code USER} et le statut {@code ACTIVE} par d&eacute;faut,
     * puis g&eacute;n&egrave;re un jeton JWT pour connecter l'utilisateur imm&eacute;diatement apr&egrave;s l'inscription.
     * </p>
     *
     * @param request les informations d'inscription (e-mail, mot de passe)
     * @return un objet {@link AuthResponse} contenant le jeton JWT, l'e-mail et le r&ocirc;le
     * @throws RuntimeException si l'e-mail est d&eacute;j&agrave; utilis&eacute; ou si le r&ocirc;le/statut par d&eacute;faut n'est pas configur&eacute;
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(roleRepository.findByLabel("USER")
                .orElseThrow(() -> new RuntimeException("Rôle par défaut non configuré")));
        user.setStatus(statusRepository.findByLabel("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Statut par défaut non configuré")));

        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }

    /**
     * Authentifie un utilisateur existant.
     * <p>
     * Proc&egrave;de en trois &eacute;tapes :
     * <ol>
     *   <li>Authentification via Spring Security ({@link AuthenticationManager})</li>
     *   <li>R&eacute;cup&eacute;ration de l'utilisateur en base de donn&eacute;es</li>
     *   <li>G&eacute;n&eacute;ration d'un jeton JWT</li>
     * </ol>
     * </p>
     *
     * @param email    l'adresse e-mail de l'utilisateur
     * @param password le mot de passe en clair
     * @return un objet {@link AuthResponse} contenant le jeton JWT, l'e-mail et le r&ocirc;le
     * @throws RuntimeException si l'utilisateur n'est pas trouv&eacute; ou si l'authentification &eacute;choue
     */
    @Override
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }
}