package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/profil")
public class ProfilController {
    private final IProfilService profilService;

    public ProfilController(IProfilService profilService) {
        this.profilService = profilService;
    }

    @PostMapping("/create")
    public ResponseEntity<Profil> create(@RequestBody Profil profil, Principal principal) {
        return ResponseEntity.ok(profilService.createProfil(profil, principal.getName()));
    }
}