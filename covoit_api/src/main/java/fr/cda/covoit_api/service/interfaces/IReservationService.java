package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.UserRoute;
import fr.cda.covoit_api.dto.response.ReservationResponse;

import java.util.List;

public interface IReservationService {
    ReservationResponse reservePlace(Integer routeId, String passengerEmail);
    void cancelReservation(Integer routeId, String passengerEmail);
    List<ReservationResponse> getPassengerReservations(String email);
}