package fr.cda.covoit_api.dto.response;

import lombok.Data;

@Data
public class VehicleResponse {
    private Integer id;
    private Short seats;
    private String carregistration;
    private String additionalInfo;
    private String modelName;
    private String brandName;
}