package co.ufps.clinica.api.appointment;

import co.ufps.clinica.adapters.appointment.JoinWaitlistRequest;
import co.ufps.clinica.adapters.appointment.WaitlistController;
import co.ufps.clinica.adapters.appointment.WaitlistResponse;
import co.ufps.clinica.api.security.RequestPrincipal;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/api/waitlist")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequireRoles({Role.PACIENTE})
public class WaitlistResource {
    @Inject WaitlistController controller;
    @POST public Response join(JoinWaitlistRequest request, @Context SecurityContext security) {
        return Response.status(Response.Status.CREATED).entity(controller.join(principal(security), request)).build();
    }
    @GET @Path("/mine") public List<WaitlistResponse> mine(@Context SecurityContext security) {
        return controller.mine(principal(security));
    }
    @DELETE @Path("/{id}") public WaitlistResponse cancel(@PathParam("id") String id, @Context SecurityContext security) {
        return controller.cancel(principal(security), id);
    }
    private co.ufps.clinica.domain.identity.UserId principal(SecurityContext security) {
        return ((RequestPrincipal) security.getUserPrincipal()).user().userId();
    }
}
