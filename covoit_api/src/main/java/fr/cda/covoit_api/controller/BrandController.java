package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Brand;
import fr.cda.covoit_api.dto.request.BrandRequest;
import fr.cda.covoit_api.dto.response.BrandResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.BrandRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAll() {
        return ResponseEntity.ok(brandRepository.findAll().stream()
                .map(entityMapper::toBrandResponse).toList());
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // Optionnel si configuré dans SecurityConfig
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest dto) {
        if (brandRepository.findByLabel(dto.getLabel()).isPresent()) {
            throw new BusinessException("Cette marque existe déjà", HttpStatus.CONFLICT);
        }
        Brand brand = new Brand();
        brand.setLabel(dto.getLabel());
        return new ResponseEntity<>(entityMapper.toBrandResponse(brandRepository.save(brand)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> update(@PathVariable Integer id, @Valid @RequestBody BrandRequest dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Marque non trouvée", HttpStatus.NOT_FOUND));
        brand.setLabel(dto.getLabel());
        return ResponseEntity.ok(entityMapper.toBrandResponse(brandRepository.save(brand)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new BusinessException("Marque non trouvée", HttpStatus.NOT_FOUND);
        }
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}