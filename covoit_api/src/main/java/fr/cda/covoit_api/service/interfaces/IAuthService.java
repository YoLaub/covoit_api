package fr.cda.covoit_api.service.interfaces;

import fr.cda.covoit_api.dto.request.RegisterRequest;
import fr.cda.covoit_api.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(String email, String password);
}