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
    private String driverName;
    private LocationResponse departure;
    private LocationResponse arrival;
}