package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.dto.request.ProfilRequest;
import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class ProfilController {

    private final IProfilService profilService;
    private final EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<ProfilResponse> create(@RequestBody ProfilRequest dto, Principal principal) {
        Profil saved = profilService.createProfil(dto, principal.getName());
        return new ResponseEntity<>(entityMapper.toProfilResponse(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfilResponse> update(@PathVariable Integer id, @RequestBody ProfilRequest dto, Principal principal) {
        Profil updated = profilService.updateProfil(id, dto, principal.getName());
        return ResponseEntity.ok(entityMapper.toProfilResponse(updated));
    }
}