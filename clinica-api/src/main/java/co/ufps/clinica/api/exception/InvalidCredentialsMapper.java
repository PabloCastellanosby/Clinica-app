package co.ufps.clinica.api.exception;

import co.ufps.clinica.application.identity.InvalidCredentialsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class InvalidCredentialsMapper implements ExceptionMapper<InvalidCredentialsException> {
    @Override
    public Response toResponse(InvalidCredentialsException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer")
                .entity(Map.of("error", exception.getMessage()))
                .build();
    }
}
