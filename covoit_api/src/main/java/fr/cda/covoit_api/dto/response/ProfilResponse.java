package fr.cda.covoit_api.dto.response;

import lombok.Data;

@Data
public class ProfilResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
}
