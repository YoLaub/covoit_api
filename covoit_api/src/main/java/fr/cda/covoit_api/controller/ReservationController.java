package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.UserRoute;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.ReservationResponse;
import fr.cda.covoit_api.service.interfaces.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class ReservationController {

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{id}/person")
    public ResponseEntity<ReservationResponse> reserve(@PathVariable Integer id, Principal principal) {
        return ResponseEntity.ok(reservationService.reservePlace(id, principal.getName()));
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Principal principal) {
        return ResponseEntity.ok(reservationService.getPassengerReservations(principal.getName()));
    }

    @DeleteMapping("/{id}/person")
    public ResponseEntity<Void> cancel(@PathVariable Integer id, Principal principal) {
        reservationService.cancelReservation(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/person")
    public ResponseEntity<List<ProfilResponse>> getRoutePassengers(@PathVariable Integer id) {
        return ResponseEntity.ok(reservationService.getPassengersByRouteId(id));
    }

}