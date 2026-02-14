package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "Le nom de la marque est obligatoire")
    private String label;
}
