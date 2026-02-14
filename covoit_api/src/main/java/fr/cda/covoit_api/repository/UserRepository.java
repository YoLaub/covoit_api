package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.domain.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
    boolean existsByEmail(String email);
}