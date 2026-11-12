package co.ufps.clinica.api.security;

import co.ufps.clinica.application.identity.AccountService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import java.util.Optional;

@ApplicationScoped
public class AdministratorBootstrap {
    private static final Logger LOG = Logger.getLogger(AdministratorBootstrap.class);
    @ConfigProperty(name = "clinica.bootstrap.admin.email") Optional<String> email;
    @ConfigProperty(name = "clinica.bootstrap.admin.password") Optional<String> password;

    void onStart(@Observes StartupEvent event, AccountService accounts) {
        String configuredEmail = email.orElse("").trim();
        String configuredPassword = password.orElse("");
        if (configuredEmail.isBlank() && configuredPassword.isBlank()) {
            LOG.warn("No se configuró administrador inicial; las operaciones protegidas requieren una cuenta ya creada.");
            return;
        }
        if (configuredEmail.isBlank() || configuredPassword.isBlank()) throw new IllegalStateException("Debe configurar email y contraseña del administrador inicial");
        accounts.bootstrapAdministrator(configuredEmail, configuredPassword);
        LOG.info("Cuenta administradora inicial verificada.");
    }
}
