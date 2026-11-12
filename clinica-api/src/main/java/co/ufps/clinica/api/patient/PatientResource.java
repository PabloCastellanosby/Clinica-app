package co.ufps.clinica.api.patient;

import co.ufps.clinica.adapters.patient.PatientController;
import co.ufps.clinica.adapters.patient.PatientResponse;
import co.ufps.clinica.adapters.patient.RegisterPatientRequest;
import co.ufps.clinica.adapters.patient.UpdatePatientRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.api.security.RequestPrincipal;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/patients")
@RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {
    @Inject PatientController patientController;
    @POST public Response register(RegisterPatientRequest request) {
        PatientResponse response = patientController.register(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    @GET public List<PatientResponse> search(@QueryParam("q") String query) { return patientController.search(query); }
    @GET @Path("/me") @RequireRoles({Role.PACIENTE})
    public PatientResponse profile(@Context SecurityContext security) {
        return patientController.profile(((RequestPrincipal) security.getUserPrincipal()).user().userId());
    }
    @PUT @Path("/me") @RequireRoles({Role.PACIENTE})
    public PatientResponse updateOwnProfile(UpdatePatientRequest request, @Context SecurityContext security) {
        return patientController.updateOwnProfile(((RequestPrincipal) security.getUserPrincipal()).user().userId(), request);
    }
    @GET @Path("/{id}") @RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
    public PatientResponse findById(@PathParam("id") String id) { return patientController.findById(id); }
    @PUT @Path("/{id}") @RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
    public PatientResponse update(@PathParam("id") String id, UpdatePatientRequest request) { return patientController.update(id, request); }
}
