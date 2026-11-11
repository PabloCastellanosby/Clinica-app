package co.ufps.clinica.adapters.communication;

public record SendMessageRequest(String recipientId, String destination, String subject, String template) {}