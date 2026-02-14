package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfilRequest {
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstname;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastname;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^(\\+33|0)[1-9](\\d{2}){4}$", message = "Format de téléphone invalide")
    private String phone;
}