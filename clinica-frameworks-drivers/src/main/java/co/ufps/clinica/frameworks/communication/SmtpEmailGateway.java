package co.ufps.clinica.frameworks.communication;

import co.ufps.clinica.application.port.out.communication.EmailGateway;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SmtpEmailGateway implements EmailGateway {
    @Inject Mailer mailer;
    @ConfigProperty(name = "clinica.mail.from", defaultValue = "noreply@clinica.local") String from;

    @Override public void send(String recipient, String subject, String body) {
        mailer.send(Mail.withText(recipient, subject, body).setFrom(from));
    }
}
