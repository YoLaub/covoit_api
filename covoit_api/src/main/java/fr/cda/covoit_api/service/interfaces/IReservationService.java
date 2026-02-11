package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.domain.entity.UserRoute;
import java.util.List;

public interface IReservationService {
    UserRoute reservePlace(Integer routeId, String passengerEmail);
    void cancelReservation(Integer routeId, String passengerEmail);
    List<UserRoute> getPassengerReservations(String email);
}