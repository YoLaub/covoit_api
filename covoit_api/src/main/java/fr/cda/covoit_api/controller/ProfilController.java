package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.dto.request.ProfilRequest;
import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.RouteResponse;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class ProfilController {

    private final IProfilService profilService;
    private final EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<ProfilResponse> create(@Valid @RequestBody ProfilRequest dto, Principal principal) {
        Profil saved = profilService.createProfil(dto, principal.getName());
        return new ResponseEntity<>(entityMapper.toProfilResponse(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfilResponse> update(@PathVariable Integer id,@Valid @RequestBody ProfilRequest dto, Principal principal) {
        Profil updated = profilService.updateProfil(id, dto, principal.getName());
        return ResponseEntity.ok(entityMapper.toProfilResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        profilService.deleteProfil(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/trips-driver")
    public ResponseEntity<List<RouteResponse>> getTripsAsDriver(@PathVariable Integer id) {
        return ResponseEntity.ok(profilService.getDriverTrips(id));
    }

    @GetMapping("/{id}/trips-passenger")
    public ResponseEntity<List<RouteResponse>> getTripsAsPassenger(@PathVariable Integer id) {
        return ResponseEntity.ok(profilService.getPassengerTrips(id));
    }

    @GetMapping
// @PreAuthorize("hasRole('ADMIN')") // Optionnel selon votre config de sécurité
    public ResponseEntity<List<ProfilResponse>> getAll() {
        return ResponseEntity.ok(profilService.getAllProfils().stream()
                .map(entityMapper::toProfilResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfilResponse> getById(@PathVariable Integer id) {
        Profil profil = profilService.getProfilById(id);
        return ResponseEntity.ok(entityMapper.toProfilResponse(profil));
    }
}