package co.ufps.clinica.api.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/** Preserva códigos HTTP JAX-RS, por ejemplo 404 en rutas inexistentes. */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse() == null ? 500 : exception.getResponse().getStatus();
        String message = status >= 500 ? "Ocurrió un error interno" : Response.Status.fromStatusCode(status).getReasonPhrase();
        return Response.status(status).entity(Map.of("error", message)).build();
    }
}
