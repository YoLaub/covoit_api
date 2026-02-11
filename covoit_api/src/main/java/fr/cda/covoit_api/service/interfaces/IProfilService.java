package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.domain.entity.Vehicle;

public interface IProfilService {
    Profil createProfil(Profil profil, String userEmail);
    Vehicle addVehicle(Vehicle vehicle, Integer profilId);
    Profil getProfilByEmail(String email);
}