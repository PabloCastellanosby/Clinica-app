package co.ufps.clinica.api.patient;

import co.ufps.clinica.adapters.patient.PatientController;
import co.ufps.clinica.adapters.patient.PatientResponse;
import co.ufps.clinica.adapters.patient.RegisterPatientRequest;
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

@Path("/api/patients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {
    @Inject PatientController patientController;
    @POST public Response register(RegisterPatientRequest request) {
        PatientResponse response = patientController.register(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    @GET public List<PatientResponse> search(@QueryParam("q") String query) { return patientController.search(query); }
}
