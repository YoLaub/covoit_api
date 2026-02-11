package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.repository.ProfilRepository;
import fr.cda.covoit_api.repository.UserRepository;
import fr.cda.covoit_api.repository.VehicleRepository;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements IProfilService {
    private final ProfilRepository profilRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Profil createProfil(Profil profil, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        profil.setUser(user);
        return profilRepository.save(profil);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle, Integer profilId) {
        if (vehicleRepository.existsByOwnerId(profilId)) {
            throw new RuntimeException("L'utilisateur possède déjà un véhicule.");
        }
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
        vehicle.setOwner(profil);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Profil getProfilByEmail(String email) {
        return null;
    }
}