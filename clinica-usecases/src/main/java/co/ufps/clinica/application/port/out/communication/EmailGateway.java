package co.ufps.clinica.application.port.out.communication;

public interface EmailGateway {
    void send(String recipient, String subject, String body);
}
