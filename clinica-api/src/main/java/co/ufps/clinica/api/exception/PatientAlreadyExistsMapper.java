package co.ufps.clinica.api.exception;

import co.ufps.clinica.domain.exception.PatientAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class PatientAlreadyExistsMapper implements ExceptionMapper<PatientAlreadyExistsException> {
    @Override public Response toResponse(PatientAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT).entity(Map.of("error", exception.getMessage())).build();
    }
}
