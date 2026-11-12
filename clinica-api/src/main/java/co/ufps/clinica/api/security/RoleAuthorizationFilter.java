package co.ufps.clinica.api.security;

import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import jakarta.inject.Inject;
import java.util.Map;

@Provider
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION)
public class RoleAuthorizationFilter implements ContainerRequestFilter {
    @Context ResourceInfo resourceInfo;
    @Inject RolePermissionRepository permissions;

    @Override public void filter(ContainerRequestContext context) throws IOException {
        if (resourceInfo == null) return;
        if (resourceInfo.getResourceMethod().isAnnotationPresent(PublicEndpoint.class)
                || resourceInfo.getResourceClass().isAnnotationPresent(PublicEndpoint.class)) return;
        RequireRoles rule = resourceInfo.getResourceMethod().getAnnotation(RequireRoles.class);
        if (rule == null) rule = resourceInfo.getResourceClass().getAnnotation(RequireRoles.class);
        if (context.getSecurityContext().getUserPrincipal() == null) return;
        if (rule == null) {
            deny(context);
            return;
        }
        Role role = ((RequestPrincipal) context.getSecurityContext().getUserPrincipal()).user().role();
        boolean permitted = role == Role.ADMINISTRADOR
                || permissions.isAllowed(role, RoutePermissionKey.forMethod(resourceInfo.getResourceClass(), resourceInfo.getResourceMethod()));
        if (!permitted) deny(context);
    }

    private void deny(ContainerRequestContext context) {
        context.abortWith(Response.status(Response.Status.FORBIDDEN)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .entity(Map.of("error", "No tiene permisos para esta operación")).build());
    }
}
