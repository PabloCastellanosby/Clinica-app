package co.ufps.clinica.api.security;

import co.ufps.clinica.application.identity.AuthenticatedUser;
import co.ufps.clinica.application.identity.SessionService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION)
public class BearerAuthenticationFilter implements ContainerRequestFilter {
    @Inject SessionService sessions;
    @Context ResourceInfo resourceInfo;

    @Override public void filter(ContainerRequestContext context) throws IOException {
        if (isPublic()) return;
        String authorization = context.getHeaderString(HttpHeaders.AUTHORIZATION);
        String token = authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim() : null;
        AuthenticatedUser user = sessions.authenticate(token).orElse(null);
        if (user == null) {
            context.abortWith(jakarta.ws.rs.core.Response.status(401)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                    .entity(java.util.Map.of("error", "Autenticación requerida o sesión vencida"))
                    .build());
            return;
        }
        SecurityContext original = context.getSecurityContext();
        context.setSecurityContext(new SecurityContext() {
            @Override public java.security.Principal getUserPrincipal() { return new RequestPrincipal(user); }
            @Override public boolean isUserInRole(String role) { return user.role().name().equalsIgnoreCase(role); }
            @Override public boolean isSecure() { return original != null && original.isSecure(); }
            @Override public String getAuthenticationScheme() { return "Bearer"; }
        });
    }

    private boolean isPublic() {
        if (resourceInfo == null) return false;
        return resourceInfo.getResourceMethod().isAnnotationPresent(PublicEndpoint.class)
                || resourceInfo.getResourceClass().isAnnotationPresent(PublicEndpoint.class);
    }
}
