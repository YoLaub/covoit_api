package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Vehicle;
import fr.cda.covoit_api.dto.request.ProfilRequest;

public interface IProfilService {
    Profil createProfil(ProfilRequest dto, String userEmail);
    Vehicle addVehicle(Vehicle vehicle, Integer profilId);
    Profil getProfilByEmail(String email);
    Profil updateProfil(Integer id, ProfilRequest dto, String emailRequestor);
}