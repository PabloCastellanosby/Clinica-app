package co.ufps.clinica.api.appointment;

import co.ufps.clinica.application.appointment.ReminderService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.time.Instant;

@ApplicationScoped
public class ReminderDispatcher {
    private static final Logger LOG = Logger.getLogger(ReminderDispatcher.class);
    @Inject ReminderService reminders;

    @Scheduled(every = "5m", delayed = "1m")
    void dispatch() {
        try {
            int processed = reminders.dispatchDue(Instant.now());
            if (processed > 0) LOG.infof("Procesados %d recordatorios de citas", processed);
        } catch (RuntimeException ex) {
            LOG.error("No se pudieron procesar los recordatorios de citas", ex);
        }
    }
}
