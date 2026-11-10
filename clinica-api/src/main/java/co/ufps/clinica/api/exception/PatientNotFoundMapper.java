package co.ufps.clinica.api.exception;

import co.ufps.clinica.application.patient.PatientNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class PatientNotFoundMapper implements ExceptionMapper<PatientNotFoundException> {
    @Override public Response toResponse(PatientNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", exception.getMessage())).build();
    }
}
