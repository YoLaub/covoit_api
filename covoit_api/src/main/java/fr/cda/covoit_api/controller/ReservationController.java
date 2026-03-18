package fr.cda.covoit_api.controller;

import fr.cda.covoit_api.domain.entity.Profil;
import fr.cda.covoit_api.dto.request.ContactRequest;
import fr.cda.covoit_api.dto.response.ProfilResponse;
import fr.cda.covoit_api.dto.response.ReservationResponse;
import fr.cda.covoit_api.exception.BusinessException;
import fr.cda.covoit_api.service.interfaces.IEmailService;
import fr.cda.covoit_api.service.interfaces.IProfilService;
import fr.cda.covoit_api.service.interfaces.IReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class ReservationController {

    private final IReservationService reservationService;
    private final IProfilService profilService;
    private final IEmailService emailService;

    public ReservationController(IReservationService reservationService, IProfilService profilService, IEmailService emailService) {
        this.reservationService = reservationService;
        this.profilService = profilService;
        this.emailService = emailService;
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

    @PostMapping("/{tripId}/contact")
    public ResponseEntity<Void> contactUser(
            @PathVariable Integer tripId,
            @Valid @RequestBody ContactRequest body,
            Principal principal) {

        reservationService.contactUser(body.getRecipientProfilId(), body.getSubject(), body.getHtmlContent());
        return ResponseEntity.ok().build();
    }

}