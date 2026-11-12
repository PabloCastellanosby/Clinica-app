package co.ufps.clinica.api.communication;

import co.ufps.clinica.application.communication.CommunicationService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageDispatcher {
    private static final Logger LOG = Logger.getLogger(MessageDispatcher.class);
    @Inject CommunicationService communications;

    @Scheduled(every = "1m", delayed = "30s")
    void dispatch() {
        try {
            int processed = communications.dispatchPending(100);
            if (processed > 0) LOG.infof("Procesados %d mensajes transaccionales", processed);
        } catch (RuntimeException ex) {
            LOG.error("No se pudieron procesar los mensajes transaccionales", ex);
        }
    }
}
