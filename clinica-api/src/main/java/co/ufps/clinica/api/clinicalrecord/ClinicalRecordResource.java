package co.ufps.clinica.api.clinicalrecord;

import co.ufps.clinica.adapters.clinicalrecord.*;
import co.ufps.clinica.domain.clinicalrecord.Attachment;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.api.security.RequestPrincipal;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/clinical-records")
@RequireRoles({Role.MEDICO, Role.ADMINISTRADOR})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClinicalRecordResource {
    @Inject ClinicalRecordController controller;

    @GET @Path("/mine/encounters") @RequireRoles({Role.PACIENTE})
    public List<EncounterResponse> ownVisibleEncounters(@Context SecurityContext security) {
        return controller.ownVisibleEncounters(((RequestPrincipal) security.getUserPrincipal()).user().userId());
    }
    @GET @Path("/mine/allergies") @RequireRoles({Role.PACIENTE})
    public List<AllergyResponse> ownAllergies(@Context SecurityContext security) {
        return controller.ownAllergies(((RequestPrincipal) security.getUserPrincipal()).user().userId());
    }
    @GET @Path("/mine/medical-history") @RequireRoles({Role.PACIENTE})
    public List<MedicalHistoryResponse> ownMedicalHistory(@Context SecurityContext security) {
        return controller.ownMedicalHistory(((RequestPrincipal) security.getUserPrincipal()).user().userId());
    }

    @POST @Path("/encounters")
    public Response createEncounter(CreateEncounterRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        EncounterResponse response = controller.createEncounter(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/prescriptions/{id}/pdf") @Produces("application/pdf")
    @RequireRoles({Role.PACIENTE, Role.MEDICO, Role.ADMINISTRADOR})
    public Response prescriptionPdf(@PathParam("id") String id, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        byte[] pdf = controller.prescriptionPdf(id, actor.user().userId(), actor.user().role());
        return Response.ok(pdf, "application/pdf").header("Content-Disposition", "attachment; filename=receta.pdf").build();
    }

    @POST @Path("/encounters/{id}/attachments") @RequireRoles({Role.MEDICO, Role.ADMINISTRADOR})
    public Response uploadAttachment(@PathParam("id") String encounterId, AttachmentUploadRequest request,
                                     @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        AttachmentResponse response = controller.uploadAttachment(encounterId, request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/encounters/{id}/attachments") @RequireRoles({Role.PACIENTE, Role.MEDICO, Role.ADMINISTRADOR})
    public List<AttachmentResponse> listAttachments(@PathParam("id") String encounterId, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.attachmentsByEncounter(encounterId, actor.user().userId(), actor.user().role());
    }

    @GET @Path("/attachments/{id}") @RequireRoles({Role.PACIENTE, Role.MEDICO, Role.ADMINISTRADOR})
    public Response downloadAttachment(@PathParam("id") String id, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        Attachment attachment = controller.downloadAttachment(id, actor.user().userId(), actor.user().role());
        String safeName = attachment.fileName().replaceAll("[^A-Za-z0-9._-]", "_");
        return Response.ok(attachment.content(), attachment.contentType())
                .header("Content-Disposition", "attachment; filename=\"" + safeName + "\"")
                .header("X-Content-Type-Options", "nosniff").build();
    }

    @GET @Path("/encounters/{id}")
    public EncounterResponse getEncounter(@PathParam("id") String id, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.getEncounter(id, actor.user().userId(), actor.user().role());
    }

    @GET @Path("/encounters/patient/{patientId}")
    public List<EncounterResponse> getPatientEncounters(@PathParam("patientId") String patientId, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.getPatientEncounters(patientId, actor.user().userId(), actor.user().role());
    }

    @POST @Path("/vital-signs")
    public Response recordVitalSigns(RecordVitalSignsRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        VitalSignsResponse response = controller.recordVitalSigns(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/prescriptions")
    public Response issuePrescription(IssuePrescriptionRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        PrescriptionResponse response = controller.issuePrescription(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/exam-orders")
    public Response orderExam(OrderExamRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        ExamOrderResponse response = controller.orderExam(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT @Path("/exam-orders/{id}/result") @RequireRoles({Role.MEDICO, Role.ADMINISTRADOR})
    public ExamOrderResponse recordExamResult(@PathParam("id") String id, RecordExamResultRequest request,
            @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.recordExamResult(id, request, actor.user().userId(), actor.user().role());
    }

    @GET @Path("/encounters/{id}/exam-orders") @RequireRoles({Role.PACIENTE, Role.MEDICO, Role.ADMINISTRADOR})
    public List<ExamOrderResponse> examOrdersByEncounter(@PathParam("id") String encounterId,
            @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.examOrdersByEncounter(encounterId, actor.user().userId(), actor.user().role());
    }

    @POST @Path("/allergies")
    public Response registerAllergy(RegisterAllergyRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        AllergyResponse response = controller.registerAllergy(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/allergies/patient/{patientId}")
    public List<AllergyResponse> getPatientAllergies(@PathParam("patientId") String patientId, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.getPatientAllergies(patientId, actor.user().userId(), actor.user().role());
    }

    @POST @Path("/medical-history")
    public Response addMedicalHistory(AddMedicalHistoryRequest request, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        MedicalHistoryResponse response = controller.addMedicalHistory(request, actor.user().userId(), actor.user().role());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/medical-history/patient/{patientId}")
    public List<MedicalHistoryResponse> getPatientMedicalHistory(@PathParam("patientId") String patientId, @Context SecurityContext security) {
        RequestPrincipal actor = (RequestPrincipal) security.getUserPrincipal();
        return controller.getPatientMedicalHistory(patientId, actor.user().userId(), actor.user().role());
    }
}
