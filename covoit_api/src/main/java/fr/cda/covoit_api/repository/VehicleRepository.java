package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    // Vérifie si un véhicule est déjà rattaché à cet ID de profil
    boolean existsByOwnerId(Integer profilId);
}