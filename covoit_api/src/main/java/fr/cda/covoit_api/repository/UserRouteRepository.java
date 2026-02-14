package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.UserRoute;
import fr.cda.covoit_api.domain.entity.UserRouteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, UserRouteId> {
    // Compte le nombre de réservations actives (non annulées) pour un trajet
    long countByRouteIdAndStatusNot(Integer routeId, String status);

    // Récupère les réservations d'un utilisateur
    List<UserRoute> findByPassengerId(Integer profilId);

    // Récupère tous les passagers d'un trajet spécifique
    List<UserRoute> findByRouteId(Integer routeId);

    List<UserRoute> findByPassengerIdAndStatusNot(Integer id, String cancelled);
}