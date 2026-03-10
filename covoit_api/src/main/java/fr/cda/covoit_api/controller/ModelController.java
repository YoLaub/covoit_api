package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Brand;
import fr.cda.covoit_api.domain.entity.Model;
import fr.cda.covoit_api.dto.request.ModelRequest;
import fr.cda.covoit_api.dto.response.ModelResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.mapper.EntityMapper;
import fr.cda.covoit_api.repository.BrandRepository;
import fr.cda.covoit_api.repository.ModelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAll() {
        return ResponseEntity.ok(modelRepository.findAll().stream()
                .map(entityMapper::toModelResponse).toList());
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ModelResponse>> getByBrand(@PathVariable Integer brandId) {
        return ResponseEntity.ok(modelRepository.findByBrandId(brandId).stream()
                .map(entityMapper::toModelResponse).toList());
    }

    @PostMapping
    public ResponseEntity<ModelResponse> create(@Valid @RequestBody ModelRequest dto) {
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new BusinessException("Marque non trouvée", HttpStatus.NOT_FOUND));
        Model model = new Model();
        model.setLabel(dto.getLabel());
        model.setBrand(brand);
        return new ResponseEntity<>(entityMapper.toModelResponse(modelRepository.save(model)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModelResponse> update(@PathVariable Integer id, @Valid @RequestBody ModelRequest dto) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Modèle non trouvé", HttpStatus.NOT_FOUND));
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new BusinessException("Marque non trouvée", HttpStatus.NOT_FOUND));
        model.setLabel(dto.getLabel());
        model.setBrand(brand);
        return ResponseEntity.ok(entityMapper.toModelResponse(modelRepository.save(model)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!modelRepository.existsById(id)) {
            throw new BusinessException("Modèle non trouvé", HttpStatus.NOT_FOUND);
        }
        modelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}