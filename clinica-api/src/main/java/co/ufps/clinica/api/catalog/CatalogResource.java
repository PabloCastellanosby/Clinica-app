package co.ufps.clinica.api.catalog;

import co.ufps.clinica.adapters.catalog.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import co.ufps.clinica.api.security.PublicEndpoint;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;

@Path("/api/catalog")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {
    @Inject CatalogController catalogController;

    @POST @Path("/specialties") @RequireRoles({Role.ADMINISTRADOR})
    public Response createSpecialty(CreateSpecialtyRequest request) {
        SpecialtyResponse response = catalogController.createSpecialty(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/specialties") @PublicEndpoint
    public List<SpecialtyResponse> getAllActiveSpecialties() {
        return catalogController.getAllActiveSpecialties();
    }

    @POST @Path("/services") @RequireRoles({Role.ADMINISTRADOR})
    public Response createService(CreateServiceRequest request) {
        ServiceResponse response = catalogController.createService(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/services") @PublicEndpoint
    public List<ServiceResponse> getAllActiveServices() {
        return catalogController.getAllActiveServices();
    }

    @GET @Path("/services/specialty/{specialtyId}") @PublicEndpoint
    public List<ServiceResponse> getServicesBySpecialty(@PathParam("specialtyId") String specialtyId) {
        return catalogController.getServicesBySpecialty(specialtyId);
    }
}
