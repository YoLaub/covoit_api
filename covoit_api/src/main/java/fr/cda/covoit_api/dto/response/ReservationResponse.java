package fr.cda.covoit_api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Integer routeId;
    private String status;
    private LocalDateTime createdAt;
    private String departureCity;
    private String arrivalCity;
    private String tripDate;
    private String driverName;
}