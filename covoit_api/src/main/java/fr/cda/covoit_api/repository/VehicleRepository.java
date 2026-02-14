package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    boolean existsByOwnerId(Integer profilId);

    // Recherche le véhicule par l'email du compte utilisateur
    Optional<Vehicle> findByOwnerUserEmail(String email);
}