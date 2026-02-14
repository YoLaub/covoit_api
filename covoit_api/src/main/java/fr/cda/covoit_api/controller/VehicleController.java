package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.*;
import fr.cda.covoit_api.dto.request.VehicleRequest;
import fr.cda.covoit_api.dto.response.VehicleResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.ModelRepository;
import fr.cda.covoit_api.repository.ProfilRepository;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class VehicleController {

    private final IProfilService profilService;
    private final ProfilRepository profilRepository;
    private final ModelRepository modelRepository;
    private final EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest dto, Principal principal) {
        Profil profil = profilRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new BusinessException("Profil non trouvé", HttpStatus.NOT_FOUND));

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new BusinessException("Modèle non trouvé", HttpStatus.NOT_FOUND));

        Vehicle vehicle = entityMapper.toVehicle(dto, model);
        Vehicle saved = profilService.addVehicle(vehicle, profil.getId());
        return new ResponseEntity<>(entityMapper.toVehicleResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping("/my-car")
    public ResponseEntity<VehicleResponse> getMyVehicle(Principal principal) {
        Vehicle v = profilService.getVehicleByEmail(principal.getName());
        return ResponseEntity.ok(entityMapper.toVehicleResponse(v));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable Integer id, @Valid @RequestBody VehicleRequest dto, Principal principal) {
        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new BusinessException("Modèle non trouvé", HttpStatus.NOT_FOUND));

        Vehicle details = entityMapper.toVehicle(dto, model);
        Vehicle updated = profilService.updateVehicle(id, details, principal.getName());
        return ResponseEntity.ok(entityMapper.toVehicleResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        profilService.deleteVehicle(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}