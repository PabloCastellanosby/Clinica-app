package co.ufps.clinica.api.identity;

import co.ufps.clinica.adapters.identity.*;
import co.ufps.clinica.api.security.PublicEndpoint;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.api.security.RequestPrincipal;
import co.ufps.clinica.domain.identity.Role;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IdentityResource {
    @Inject IdentityController controller;

    @POST @Path("/register") @PublicEndpoint
    public Response register(RegisterAccountRequest request) {
        return Response.status(Response.Status.CREATED).entity(java.util.Map.of("userId", controller.registerPatient(request))).build();
    }

    @POST @Path("/login") @PublicEndpoint
    public LoginResponse login(LoginRequest request, @Context io.vertx.ext.web.RoutingContext routingContext) {
        var remoteAddress = routingContext.request().remoteAddress();
        return controller.login(request, remoteAddress == null ? null : remoteAddress.hostAddress());
    }

    @POST @Path("/logout")
    public Response logout(@Context HttpHeaders headers) {
        String authorization = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        String token = authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim() : null;
        controller.logout(token);
        return Response.noContent().build();
    }

    @POST @Path("/staff") @RequireRoles({Role.ADMINISTRADOR})
    public Response provisionStaff(StaffAccountRequest request, @Context jakarta.ws.rs.core.SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        String userId = controller.provisionStaff(actor.user(), request);
        return Response.status(Response.Status.CREATED).entity(java.util.Map.of("userId", userId)).build();
    }

    @jakarta.ws.rs.PUT @Path("/staff/{userId}/status") @RequireRoles({Role.ADMINISTRADOR})
    public Response setStaffActive(@jakarta.ws.rs.PathParam("userId") String userId,
            @jakarta.ws.rs.QueryParam("active") boolean active,
            @Context jakarta.ws.rs.core.SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        var user = controller.setStaffActive(actor.user(), userId, active);
        return Response.ok(java.util.Map.of("userId", user.id().value().toString(), "active", user.active())).build();
    }

    @jakarta.ws.rs.PUT @Path("/staff/{userId}/role") @Consumes(MediaType.APPLICATION_JSON)
    @RequireRoles({Role.ADMINISTRADOR})
    public Response changeStaffRole(@jakarta.ws.rs.PathParam("userId") String userId,
            StaffRoleRequest request, @Context jakarta.ws.rs.core.SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        var user = controller.changeStaffRole(actor.user(), userId, request.role());
        return Response.ok(java.util.Map.of("userId", user.id().value().toString(), "role", user.role().name())).build();
    }

    @POST @Path("/password-reset/request") @PublicEndpoint
    public Response requestPasswordReset(PasswordResetRequest request) {
        controller.requestPasswordReset(request);
        return Response.accepted(java.util.Map.of("message", "Si la cuenta existe, recibirá instrucciones de recuperación")).build();
    }

    @POST @Path("/password-reset/confirm") @PublicEndpoint
    public Response confirmPasswordReset(ConfirmPasswordResetRequest request) {
        controller.confirmPasswordReset(request);
        return Response.noContent().build();
    }
}
