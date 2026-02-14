package fr.cda.covoit_api.dto.request;

import lombok.Data;

@Data
public class ProfilRequest {
    private String firstname;
    private String lastname;
    private String phone;
}