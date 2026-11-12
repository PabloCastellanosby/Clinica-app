package co.ufps.clinica.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<SecurityException> {
    @Override public Response toResponse(SecurityException exception) {
        return Response.status(Response.Status.FORBIDDEN).entity(Map.of("error", exception.getMessage())).build();
    }
}
