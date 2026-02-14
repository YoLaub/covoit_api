package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.service.interfaces.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {

    @Value("${brevo.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    @Override
    public void sendSimpleMessage(String to, String subject, String content) {
        log.info("Tentative d'envoi email à : {}", to);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Clé Brevo non configurée, email non envoyé");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "Covoit GRETA", "email", "johngreta904@gmail.com"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("templateId", 4);
        body.put("params", Map.of(
                "subject", subject,
                "content", content
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email envoyé avec succès via Brevo");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi Brevo : {}", e.getMessage());
        }
    }
}