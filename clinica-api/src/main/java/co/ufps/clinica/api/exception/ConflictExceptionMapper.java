package co.ufps.clinica.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<IllegalStateException> {
    @Override public Response toResponse(IllegalStateException exception) {
        return Response.status(Response.Status.CONFLICT).entity(Map.of("error", exception.getMessage())).build();
    }
}
