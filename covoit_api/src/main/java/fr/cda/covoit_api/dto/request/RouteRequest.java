package fr.cda.covoit_api.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RouteRequest {
    private Integer kms;
    private Short availableSeats;
    private LocalDate tripDate;
    private LocalTime tripHour;
    private AddressRequest startingAddress;
    private AddressRequest arrivalAddress;

    @Data
    public static class AddressRequest {
        private String streetNumber;
        private String streetName;
        private String postalCode;
        private String city;
        private Double latitude;
        private Double longitude;
    }
}