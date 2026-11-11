package co.ufps.clinica.api.clinicalrecord;

import co.ufps.clinica.adapters.clinicalrecord.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/clinical-records")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClinicalRecordResource {
    @Inject ClinicalRecordController controller;

    @POST @Path("/encounters")
    public Response createEncounter(CreateEncounterRequest request) {
        EncounterResponse response = controller.createEncounter(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/encounters/{id}")
    public EncounterResponse getEncounter(@PathParam("id") String id) {
        return controller.getEncounter(id);
    }

    @GET @Path("/encounters/patient/{patientId}")
    public List<EncounterResponse> getPatientEncounters(@PathParam("patientId") String patientId) {
        return controller.getPatientEncounters(patientId);
    }

    @POST @Path("/vital-signs")
    public Response recordVitalSigns(RecordVitalSignsRequest request) {
        VitalSignsResponse response = controller.recordVitalSigns(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/prescriptions")
    public Response issuePrescription(IssuePrescriptionRequest request) {
        PrescriptionResponse response = controller.issuePrescription(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/exam-orders")
    public Response orderExam(OrderExamRequest request) {
        ExamOrderResponse response = controller.orderExam(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/allergies")
    public Response registerAllergy(RegisterAllergyRequest request) {
        AllergyResponse response = controller.registerAllergy(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/allergies/patient/{patientId}")
    public List<AllergyResponse> getPatientAllergies(@PathParam("patientId") String patientId) {
        return controller.getPatientAllergies(patientId);
    }

    @POST @Path("/medical-history")
    public Response addMedicalHistory(AddMedicalHistoryRequest request) {
        MedicalHistoryResponse response = controller.addMedicalHistory(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/medical-history/patient/{patientId}")
    public List<MedicalHistoryResponse> getPatientMedicalHistory(@PathParam("patientId") String patientId) {
        return controller.getPatientMedicalHistory(patientId);
    }
}