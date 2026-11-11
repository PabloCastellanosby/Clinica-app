package co.ufps.clinica.api.staff;

import co.ufps.clinica.adapters.staff.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/doctors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DoctorResource {
    @Inject DoctorController doctorController;

    @POST @Path("/register")
    public Response registerDoctor(RegisterDoctorRequest request) {
        DoctorResponse response = doctorController.registerDoctor(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT @Path("/{id}/status")
    public DoctorResponse updateStatus(@PathParam("id") String id, @QueryParam("status") String status) {
        return doctorController.updateStatus(id, status);
    }

    @GET @Path("/active")
    public List<DoctorResponse> getAllActiveDoctors() {
        return doctorController.getAllActiveDoctors();
    }

    @GET @Path("/specialty/{specialtyId}")
    public List<DoctorResponse> getDoctorsBySpecialty(@PathParam("specialtyId") String specialtyId) {
        return doctorController.getDoctorsBySpecialty(specialtyId);
    }

    @POST @Path("/availability")
    public Response setAvailability(SetAvailabilityRequest request) {
        AvailabilityResponse response = doctorController.setAvailability(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/{id}/availability")
    public List<AvailabilityResponse> getDoctorAvailability(@PathParam("id") String doctorId) {
        return doctorController.getDoctorAvailability(doctorId);
    }
}