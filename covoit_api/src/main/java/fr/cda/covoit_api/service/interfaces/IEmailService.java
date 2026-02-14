package fr.cda.covoit_api.service.interfaces;

public interface IEmailService {
    void sendSimpleMessage(String to, String subject, String content);
}