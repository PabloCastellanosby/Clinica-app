package co.ufps.clinica.api.clinic;

import co.ufps.clinica.adapters.clinic.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/clinics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClinicResource {
    @Inject ClinicController clinicController;

    @POST @Path("/register")
    public Response registerClinic(RegisterClinicRequest request) {
        ClinicResponse response = clinicController.registerClinic(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/active")
    public List<ClinicResponse> getAllActiveClinics() {
        return clinicController.getAllActiveClinics();
    }

    @POST @Path("/branches")
    public Response registerBranch(RegisterBranchRequest request) {
        BranchResponse response = clinicController.registerBranch(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/branches/clinic/{clinicId}")
    public List<BranchResponse> getBranchesByClinic(@PathParam("clinicId") String clinicId) {
        return clinicController.getBranchesByClinic(clinicId);
    }

    @POST @Path("/offices")
    public Response registerOffice(RegisterOfficeRequest request) {
        OfficeResponse response = clinicController.registerOffice(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/offices/branch/{branchId}")
    public List<OfficeResponse> getOfficesByBranch(@PathParam("branchId") String branchId) {
        return clinicController.getOfficesByBranch(branchId);
    }

    @POST @Path("/holidays")
    public Response createHoliday(CreateHolidayRequest request) {
        HolidayResponse response = clinicController.createHoliday(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}