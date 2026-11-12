package co.ufps.clinica.api.security;

import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.domain.identity.AccessAudit;
import co.ufps.clinica.domain.identity.AccessAuditId;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;
import java.io.IOException;
import java.time.Instant;

/** Persiste el resultado real de operaciones autenticadas, una vez conocidas las respuestas HTTP. */
@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class AccessAuditResponseFilter implements ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(AccessAuditResponseFilter.class);
    @Inject UserRepository users;
    @Inject RoutingContext routingContext;

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (!(request.getSecurityContext().getUserPrincipal() instanceof RequestPrincipal)
                && response.getStatus() != 401) return;

        String path = request.getUriInfo().getPath();
        String action = request.getMethod() + " " + (path == null ? "" : path);
        if (action.length() > 80) action = action.substring(0, 80);
        co.ufps.clinica.domain.identity.UserId userId = request.getSecurityContext().getUserPrincipal() instanceof RequestPrincipal principal
                ? principal.user().userId() : null;
        var remoteAddress = routingContext == null ? null : routingContext.request().remoteAddress();
        String ipAddress = remoteAddress == null ? null : remoteAddress.hostAddress();
        try {
            users.saveAudit(new AccessAudit(AccessAuditId.generate(), userId, action,
                    ipAddress, Instant.now(), response.getStatus() < 400));
        } catch (RuntimeException ignored) {
            LOG.error("No se pudo guardar la auditoría del acceso autenticado", ignored);
        }
    }
}
