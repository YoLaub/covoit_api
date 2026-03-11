package fr.cda.covoit_api.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RouteResponse {
    private Integer id;
    private Integer kms;
    private Short availableSeats;
    private LocalDate date;
    private LocalTime hour;
    private String iconLabel;
    private String driverName;
    private LocationResponse departure;
    private LocationResponse arrival;

    // Détails conducteur
    private DriverInfo driver;

    // Détails véhicule (nullable si pas de véhicule)
    private VehicleInfo vehicle;

    @Data
    public static class DriverInfo {
        private Integer profilId;
        private String firstname;
        private String lastname;
        private String phone;
        private String email;
    }

    @Data
    public static class VehicleInfo {
        private Integer id;
        private String brand;
        private String model;
        private Short seats;
        private String carregistration;
    }
}