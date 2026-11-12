package co.ufps.clinica.adapters.clinicalrecord;

import co.ufps.clinica.application.clinicalrecord.*;
import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.application.documents.PrescriptionDocumentService;
import java.util.List;
import java.util.Objects;

public final class ClinicalRecordController {
    private final ClinicalRecordService service;
    private final PatientRepository patients;
    private final PrescriptionDocumentService documents;
    public ClinicalRecordController(ClinicalRecordService service, PatientRepository patients, PrescriptionDocumentService documents) {
        this.service = Objects.requireNonNull(service);
        this.patients = Objects.requireNonNull(patients);
        this.documents = Objects.requireNonNull(documents);
    }

    public byte[] prescriptionPdf(String id, UserId actorId, Role role) {
        return documents.pdfForUser(co.ufps.clinica.domain.clinicalrecord.PrescriptionId.of(java.util.UUID.fromString(id)), actorId, role);
    }
    public AttachmentResponse uploadAttachment(String encounterId, AttachmentUploadRequest request, UserId actorId, Role role) {
        if (request == null || request.contentBase64() == null || request.contentBase64().length() > 7_000_000)
            throw new IllegalArgumentException("El archivo codificado excede el límite permitido");
        byte[] content;
        try { content = java.util.Base64.getDecoder().decode(request.contentBase64()); }
        catch (RuntimeException ex) { throw new IllegalArgumentException("El contenido debe estar codificado en Base64 válido"); }
        return presentAttachment(service.addAttachment(EncounterId.of(java.util.UUID.fromString(encounterId)),
                request.fileName(), request.contentType(), content, actorId, role));
    }
    public List<AttachmentResponse> attachmentsByEncounter(String encounterId, UserId actorId, Role role) {
        return service.attachmentsForEncounter(EncounterId.of(java.util.UUID.fromString(encounterId)), actorId, role)
                .stream().map(this::presentAttachment).toList();
    }
    public Attachment downloadAttachment(String id, UserId actorId, Role role) {
        return service.attachmentForUser(co.ufps.clinica.domain.clinicalrecord.AttachmentId.of(java.util.UUID.fromString(id)), actorId, role);
    }
    private AttachmentResponse presentAttachment(Attachment attachment) {
        return new AttachmentResponse(attachment.id().value().toString(), attachment.encounterId().value().toString(),
                attachment.fileName(), attachment.contentType(), attachment.sizeBytes(), attachment.uploadedAt());
    }

    public List<EncounterResponse> ownVisibleEncounters(UserId userId) {
        PatientId patientId = ownPatientId(userId);
        return service.getPatientEncounters(patientId, userId, Role.PACIENTE).stream().filter(Encounter::patientVisible).map(this::presentEncounter).toList();
    }
    public List<AllergyResponse> ownAllergies(UserId userId) {
        return service.getPatientAllergies(ownPatientId(userId), userId, Role.PACIENTE).stream().map(this::presentAllergy).toList();
    }
    public List<MedicalHistoryResponse> ownMedicalHistory(UserId userId) {
        return service.getPatientMedicalHistory(ownPatientId(userId), userId, Role.PACIENTE).stream().map(this::presentMedicalHistory).toList();
    }
    private PatientId ownPatientId(UserId userId) {
        return patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente")).id();
    }

    public EncounterResponse createEncounter(CreateEncounterRequest request, UserId actorId, Role actorRole) {
        requireRequest(request, "Los datos de la consulta son obligatorios");
        Encounter e = service.createEncounter(new CreateEncounterCommand(
                AppointmentId.of(java.util.UUID.fromString(request.appointmentId())), PatientId.of(java.util.UUID.fromString(request.patientId())),
                DoctorId.of(java.util.UUID.fromString(request.doctorId())), request.reason(), request.diagnosis(),
                request.notes(), request.patientVisible()), actorId, actorRole);
        return presentEncounter(e);
    }

    public VitalSignsResponse recordVitalSigns(RecordVitalSignsRequest request, UserId actorId, Role role) {
        requireRequest(request, "Los signos vitales son obligatorios");
        VitalSigns v = service.recordVitalSigns(new VitalSignsRecordCommand(
                EncounterId.of(java.util.UUID.fromString(request.encounterId())), request.temperatureC(), request.systolic(),
                request.diastolic(), request.heartRate(), request.respiratoryRate(),
                request.weightKg(), request.heightCm()), actorId, role);
        return presentVitalSigns(v);
    }

    public PrescriptionResponse issuePrescription(IssuePrescriptionRequest request, UserId actorId, Role role) {
        requireRequest(request, "Los datos de la receta son obligatorios");
        if (request.lines() == null || request.lines().isEmpty() || request.lines().stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("La receta debe incluir líneas de medicamentos válidas");
        Prescription p = service.issuePrescription(new IssuePrescriptionCommand(
                EncounterId.of(java.util.UUID.fromString(request.encounterId())), PatientId.of(java.util.UUID.fromString(request.patientId())),
                DoctorId.of(java.util.UUID.fromString(request.doctorId())), request.instructions(),
                request.lines().stream().map(l -> new PrescriptionLine(
                        PrescriptionLineId.generate(), l.medicine(), l.presentation(),
                        l.dose(), l.frequency(), l.durationDays(), l.quantity())).toList()), actorId, role);
        return presentPrescription(p);
    }

    public ExamOrderResponse orderExam(OrderExamRequest request, UserId actorId, Role role) {
        requireRequest(request, "Los datos de la orden de examen son obligatorios");
        ExamOrder e = service.orderExam(new OrderExamCommand(
                EncounterId.of(java.util.UUID.fromString(request.encounterId())), request.name(), request.instructions()), actorId, role);
        return presentExamOrder(e);
    }

    public ExamOrderResponse recordExamResult(String orderId, RecordExamResultRequest request, UserId actorId, Role role) {
        if (request == null) throw new IllegalArgumentException("El resultado del examen es obligatorio");
        return presentExamOrder(service.recordExamResult(ExamOrderId.of(java.util.UUID.fromString(orderId)),
                request.result(), actorId, role));
    }

    public List<ExamOrderResponse> examOrdersByEncounter(String encounterId, UserId actorId, Role role) {
        return service.examOrdersForEncounter(EncounterId.of(java.util.UUID.fromString(encounterId)), actorId, role)
                .stream().map(this::presentExamOrder).toList();
    }

    public AllergyResponse registerAllergy(RegisterAllergyRequest request, UserId actorId, Role role) {
        requireRequest(request, "Los datos de la alergia son obligatorios");
        Allergy a = service.registerAllergy(new RegisterAllergyCommand(
                PatientId.of(java.util.UUID.fromString(request.patientId())), request.substance(), request.reaction(), request.severity()), actorId, role);
        return presentAllergy(a);
    }

    public List<AllergyResponse> getPatientAllergies(String patientId, UserId actorId, Role role) {
        return service.getPatientAllergies(PatientId.of(java.util.UUID.fromString(patientId)), actorId, role).stream().map(this::presentAllergy).toList();
    }

    public MedicalHistoryResponse addMedicalHistory(AddMedicalHistoryRequest request, UserId actorId, Role role) {
        requireRequest(request, "Los datos del antecedente son obligatorios");
        MedicalHistory h = service.addMedicalHistory(new AddMedicalHistoryCommand(
                PatientId.of(java.util.UUID.fromString(request.patientId())), request.type(), request.description(),
                actorId), actorId, role);
        return presentMedicalHistory(h);
    }

    public List<MedicalHistoryResponse> getPatientMedicalHistory(String patientId, UserId actorId, Role role) {
        return service.getPatientMedicalHistory(PatientId.of(java.util.UUID.fromString(patientId)), actorId, role).stream().map(this::presentMedicalHistory).toList();
    }

    public List<EncounterResponse> getPatientEncounters(String patientId, UserId actorId, Role role) {
        return service.getPatientEncounters(PatientId.of(java.util.UUID.fromString(patientId)), actorId, role).stream().map(this::presentEncounter).toList();
    }

    public EncounterResponse getEncounter(String id, UserId actorId, Role role) {
        return presentEncounter(service.encounterForUser(EncounterId.of(java.util.UUID.fromString(id)), actorId, role));
    }

    private EncounterResponse presentEncounter(Encounter e) {
        return new EncounterResponse(e.id().value().toString(), e.appointmentId().value().toString(),
                e.patientId().value().toString(), e.doctorId().value().toString(),
                e.occurredAt(), e.reason(), e.diagnosis(), e.notes(), e.patientVisible());
    }

    private static void requireRequest(Object request, String message) {
        if (request == null) throw new IllegalArgumentException(message);
    }

    private VitalSignsResponse presentVitalSigns(VitalSigns v) {
        return new VitalSignsResponse(v.id().value().toString(), v.encounterId().value().toString(),
                v.temperatureC(), v.systolic(), v.diastolic(), v.heartRate(),
                v.respiratoryRate(), v.weightKg(), v.heightCm(), v.measuredAt());
    }

    private PrescriptionResponse presentPrescription(Prescription p) {
        return new PrescriptionResponse(p.id().value().toString(), p.encounterId().value().toString(),
                p.patientId().value().toString(), p.doctorId().value().toString(),
                p.issuedAt(), p.instructions(),
                p.lines().stream().map(l -> l.medicine() + " - " + l.dose() + " - " + l.frequency()).toList());
    }

    private ExamOrderResponse presentExamOrder(ExamOrder e) {
        return new ExamOrderResponse(e.id().value().toString(), e.encounterId().value().toString(),
                e.name(), e.instructions(), e.status().name(), e.orderedAt(), e.completedAt(), e.result());
    }

    private AllergyResponse presentAllergy(Allergy a) {
        return new AllergyResponse(a.id().value().toString(), a.patientId().value().toString(),
                a.substance(), a.reaction(), a.severity());
    }

    private MedicalHistoryResponse presentMedicalHistory(MedicalHistory h) {
        return new MedicalHistoryResponse(h.id().value().toString(), h.patientId().value().toString(),
                h.type(), h.description(), h.recordedAt(), h.recordedBy().value().toString());
    }
}
