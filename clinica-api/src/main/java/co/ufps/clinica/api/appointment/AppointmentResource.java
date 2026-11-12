package co.ufps.clinica.api.appointment;

import co.ufps.clinica.adapters.appointment.AppointmentController;
import co.ufps.clinica.adapters.appointment.AppointmentResponse;
import co.ufps.clinica.adapters.appointment.ScheduleAppointmentRequest;
import co.ufps.clinica.api.security.RequestPrincipal;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.staff.DoctorId;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Path("/api/appointments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppointmentResource {
    @Inject AppointmentController controller;

    @POST @RequireRoles({Role.PACIENTE})
    public Response scheduleForPatient(ScheduleAppointmentRequest request, @Context SecurityContext security) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        AppointmentResponse response = controller.scheduleForPatient(principal.user().userId(), request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/reception") @RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
    public Response scheduleForReception(ScheduleAppointmentRequest request) {
        return Response.status(Response.Status.CREATED).entity(controller.scheduleForReception(request)).build();
    }

    @POST @Path("/from-waitlist/{entryId}") @RequireRoles({Role.PACIENTE})
    public Response bookWaitlistOffer(@PathParam("entryId") String entryId, ScheduleAppointmentRequest request,
                                      @Context SecurityContext security) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        AppointmentResponse response = controller.bookWaitlistOffer(principal.user().userId(), entryId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT @Path("/{id}/status") @RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION, Role.MEDICO})
    public AppointmentResponse changeStatus(@PathParam("id") String id, @QueryParam("status") String status,
            @QueryParam("reason") String reason, @Context SecurityContext security) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        if (principal.user().role() == Role.MEDICO) return controller.changeStatusByDoctor(principal.user().userId(), id, status);
        return controller.changeStatus(id, status, reason);
    }

    @PUT @Path("/{id}/cancel") @RequireRoles({Role.PACIENTE})
    public AppointmentResponse cancelForPatient(@PathParam("id") String id,
            @QueryParam("reason") String reason, @Context SecurityContext security) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        return controller.cancelForPatient(principal.user().userId(), id, reason);
    }

    @GET @Path("/mine") @RequireRoles({Role.PACIENTE})
    public List<AppointmentResponse> patientAppointments(@Context SecurityContext security,
            @QueryParam("from") Instant from, @QueryParam("until") Instant until) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        return controller.patientAppointments(principal.user().userId(), from, until);
    }

    @GET @Path("/doctor/{doctorId}") @RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
    public List<AppointmentResponse> doctorAgenda(@PathParam("doctorId") String doctorId,
            @QueryParam("from") Instant from, @QueryParam("until") Instant until) {
        return controller.doctorAgenda(DoctorId.of(UUID.fromString(doctorId)), from, until);
    }

    @GET @Path("/doctor/mine") @RequireRoles({Role.MEDICO})
    public List<AppointmentResponse> ownDoctorAgenda(@Context SecurityContext security,
            @QueryParam("from") Instant from, @QueryParam("until") Instant until) {
        RequestPrincipal principal = (RequestPrincipal) security.getUserPrincipal();
        return controller.doctorAgenda(principal.user().userId(), from, until);
    }
}
