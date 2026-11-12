package co.ufps.clinica.api.identity;

import co.ufps.clinica.adapters.security.PermissionController;
import co.ufps.clinica.adapters.security.RolePermissionResponse;
import co.ufps.clinica.api.security.RequestPrincipal;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@RequireRoles({Role.ADMINISTRADOR})
public class RolePermissionResource {
    @Inject PermissionController controller;

    @GET @Path("/permissions")
    public List<RolePermissionResponse> list(@Context SecurityContext security) {
        return controller.list(((RequestPrincipal) security.getUserPrincipal()).user().role());
    }

    @PUT @Path("/roles/{role}/permissions/{permissionKey}")
    public Response setAllowed(@PathParam("role") String role, @PathParam("permissionKey") String permissionKey,
            @QueryParam("allowed") boolean allowed, @Context SecurityContext security) {
        Role actorRole = ((RequestPrincipal) security.getUserPrincipal()).user().role();
        controller.setAllowed(actorRole, role, permissionKey, allowed);
        return Response.ok(Map.of("role", role, "permissionKey", permissionKey, "allowed", allowed)).build();
    }
}
