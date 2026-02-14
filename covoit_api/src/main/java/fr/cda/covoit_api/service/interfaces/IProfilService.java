package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.dto.request.ProfilRequest;
import jakarta.transaction.Transactional;

public interface IProfilService {
    Profil createProfil(ProfilRequest dto, String userEmail);
    Vehicle addVehicle(Vehicle vehicle, Integer profilId);
    Profil getProfilByEmail(String email);
    Profil updateProfil(Integer id, ProfilRequest dto, String emailRequestor);

    @Transactional
    void deleteProfil(Integer id, String requestorEmail);

    Vehicle getVehicleByEmail(String email);
    Vehicle updateVehicle(Integer id, Vehicle vehicleDetails, String email);
    void deleteVehicle(Integer id, String email);
}