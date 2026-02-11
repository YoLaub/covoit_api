package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {
    // Utile pour filtrer les modèles par marque dans l'IHM plus tard
    List<Model> findByBrandId(Integer brandId);
}