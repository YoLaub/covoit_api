package fr.cda.covoit_api.service.impl;

import fr.cda.covoit_api.service.interfaces.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {

    @Override
    public void sendSimpleMessage(String to, String subject, String content) {
        // Pour l'instant, on logge simplement. On intégrera Brevo ici.
        log.info("ENVOI EMAIL à : {} | Sujet : {} | Contenu : {}", to, subject, content);
    }
}