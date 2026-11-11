package co.ufps.clinica.api.catalog;

import co.ufps.clinica.adapters.catalog.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/catalog")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {
    @Inject CatalogController catalogController;

    @POST @Path("/specialties")
    public Response createSpecialty(CreateSpecialtyRequest request) {
        SpecialtyResponse response = catalogController.createSpecialty(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/specialties")
    public List<SpecialtyResponse> getAllActiveSpecialties() {
        return catalogController.getAllActiveSpecialties();
    }

    @POST @Path("/services")
    public Response createService(CreateServiceRequest request) {
        ServiceResponse response = catalogController.createService(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/services")
    public List<ServiceResponse> getAllActiveServices() {
        return catalogController.getAllActiveServices();
    }

    @GET @Path("/services/specialty/{specialtyId}")
    public List<ServiceResponse> getServicesBySpecialty(@PathParam("specialtyId") String specialtyId) {
        return catalogController.getServicesBySpecialty(specialtyId);
    }
}