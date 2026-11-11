package co.ufps.clinica.application.communication;

import co.ufps.clinica.domain.identity.UserId;

public record SendMessageCommand(UserId recipientId, String destination,
                                 String subject, String template) {}
