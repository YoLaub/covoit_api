package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.repository.ProfilRepository;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cars") // Aligné sur Doc_API.md (avec préfixe /api)
public class VehicleController {

    private final IProfilService profilService;
    private final ProfilRepository profilRepository;

    public VehicleController(IProfilService profilService, ProfilRepository profilRepository) {
        this.profilService = profilService;
        this.profilRepository = profilRepository;
    }

    @PostMapping
    public ResponseEntity<Vehicle> create(@RequestBody Vehicle vehicle, Principal principal) {
        // 1. Récupérer le profil via l'email de l'utilisateur authentifié
        Profil profil = profilRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour l'utilisateur"));

        // 2. Appel au service qui vérifie la règle "1 seul véhicule"
        Vehicle savedVehicle = profilService.addVehicle(vehicle, profil.getId());
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    // TODO: (GET, PUT, DELETE) vérifier que le véhicule appartient bien au profil de l'utilisateur (principal.getName())

}