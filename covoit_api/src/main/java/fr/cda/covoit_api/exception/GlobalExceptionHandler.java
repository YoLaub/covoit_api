package fr.cda.covoit_api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MESSAGE = "error";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_MESSAGE, "Une erreur interne est survenue");
        error.put("details", ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_MESSAGE, "Impossible de supprimer cette ressource car elle est utilisée par d'autres éléments.");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // Code 409
    }
}