package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Integer> {
    // Permet de retrouver le profil à partir de l'email du compte UserAccount
    Optional<Profil> findByUserEmail(String email);
}