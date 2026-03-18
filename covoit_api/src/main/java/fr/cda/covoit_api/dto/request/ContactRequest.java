package fr.cda.covoit_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactRequest {
    @NotNull(message = "Le destinataire est obligatoire")
    private Integer recipientProfilId;

    @NotBlank(message = "L'objet est obligatoire")
    private String subject;

    @NotBlank(message = "Le contenu est obligatoire")
    private String htmlContent;
}