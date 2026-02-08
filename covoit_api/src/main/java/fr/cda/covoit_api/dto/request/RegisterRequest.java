package fr.cda.covoit_api.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String pseudo;
    private String email;
    private String password;
}