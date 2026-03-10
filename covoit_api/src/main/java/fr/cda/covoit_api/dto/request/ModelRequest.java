package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModelRequest {
    @NotBlank
    private String label;
    @NotNull
    private Integer brandId;
}