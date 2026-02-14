package fr.cda.covoit_api.dto.response;

import lombok.Data;

@Data
public class LocationResponse {
    private String streetNumber;
    private String streetName;
    private String postalCode;
    private String cityName;
}