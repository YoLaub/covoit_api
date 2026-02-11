package fr.cda.covoit_api.security;

import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        boolean isEnabled = "ACTIVE".equalsIgnoreCase(user.getStatus().getLabel());

        boolean isAccountNonLocked = !"BANNED".equalsIgnoreCase(user.getStatus().getLabel());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                isEnabled,
                true,
                true,
                isAccountNonLocked,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getLabel()))
        );
    }
}