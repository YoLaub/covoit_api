package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.User;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.dto.request.ProfilRequest;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.repository.ProfilRepository;
import fr.cda.covoit_api.repository.UserRepository;
import fr.cda.covoit_api.repository.VehicleRepository;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements IProfilService {
    private final ProfilRepository profilRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Profil createProfil(ProfilRequest dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Compte non trouvé", HttpStatus.NOT_FOUND));

        Profil profil = new Profil();
        profil.setFirstname(dto.getFirstname());
        profil.setLastname(dto.getLastname());
        profil.setPhone(dto.getPhone());
        profil.setUser(user);

        return profilRepository.save(profil);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle, Integer profilId) {
        if (vehicleRepository.existsByOwnerId(profilId)) {
            throw new BusinessException("L'utilisateur possède déjà un véhicule.", HttpStatus.CONFLICT);
        }
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));
        vehicle.setOwner(profil);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Profil getProfilByEmail(String email) {
        return profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));
    }

    @Override
    public Profil updateProfil(Integer id, ProfilRequest dto, String emailRequestor) {
        Profil current = profilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));

        if (!current.getUser().getEmail().equals(emailRequestor)) {
            throw new BusinessException("Action non autorisée", HttpStatus.FORBIDDEN);
        }

        current.setFirstname(dto.getFirstname());
        current.setLastname(dto.getLastname());
        current.setPhone(dto.getPhone());

        return profilRepository.save(current);
    }
}