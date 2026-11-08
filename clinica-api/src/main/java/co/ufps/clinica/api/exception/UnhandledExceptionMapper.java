package co.ufps.clinica.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/** Respuesta segura por defecto; evita que errores internos filtren detalles al cliente. */
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    @Override public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Ocurrió un error interno"))
                .build();
    }
}
