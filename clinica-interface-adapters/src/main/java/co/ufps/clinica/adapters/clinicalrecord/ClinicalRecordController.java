package co.ufps.clinica.adapters.clinicalrecord;

import co.ufps.clinica.application.clinicalrecord.*;
import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.identity.UserId;
import java.util.List;
import java.util.Objects;

public final class ClinicalRecordController {
    private final ClinicalRecordService service;
    public ClinicalRecordController(ClinicalRecordService service) { this.service = Objects.requireNonNull(service); }

    public EncounterResponse createEncounter(CreateEncounterRequest request) {
        Encounter e = service.createEncounter(new CreateEncounterCommand(
                AppointmentId.of(request.appointmentId()), PatientId.of(request.patientId()),
                DoctorId.of(request.doctorId()), request.reason(), request.diagnosis(),
                request.notes(), request.patientVisible()));
        return presentEncounter(e);
    }

    public VitalSignsResponse recordVitalSigns(RecordVitalSignsRequest request) {
        VitalSigns v = service.recordVitalSigns(new VitalSignsRecordCommand(
                EncounterId.of(request.encounterId()), request.temperatureC(), request.systolic(),
                request.diastolic(), request.heartRate(), request.respiratoryRate(),
                request.weightKg(), request.heightCm()));
        return presentVitalSigns(v);
    }

    public PrescriptionResponse issuePrescription(IssuePrescriptionRequest request) {
        Prescription p = service.issuePrescription(new IssuePrescriptionCommand(
                EncounterId.of(request.encounterId()), PatientId.of(request.patientId()),
                DoctorId.of(request.doctorId()), request.instructions(),
                request.lines().stream().map(l -> new PrescriptionLine(
                        PrescriptionLineId.generate(), l.medicine(), l.presentation(),
                        l.dose(), l.frequency(), l.durationDays(), l.quantity())).toList()));
        return presentPrescription(p);
    }

    public ExamOrderResponse orderExam(OrderExamRequest request) {
        ExamOrder e = service.orderExam(new OrderExamCommand(
                EncounterId.of(request.encounterId()), request.name(), request.instructions()));
        return presentExamOrder(e);
    }

    public AllergyResponse registerAllergy(RegisterAllergyRequest request) {
        Allergy a = service.registerAllergy(new RegisterAllergyCommand(
                PatientId.of(request.patientId()), request.substance(), request.reaction(), request.severity()));
        return presentAllergy(a);
    }

    public List<AllergyResponse> getPatientAllergies(String patientId) {
        return service.getPatientAllergies(PatientId.of(patientId)).stream().map(this::presentAllergy).toList();
    }

    public MedicalHistoryResponse addMedicalHistory(AddMedicalHistoryRequest request) {
        MedicalHistory h = service.addMedicalHistory(new AddMedicalHistoryCommand(
                PatientId.of(request.patientId()), request.type(), request.description(),
                UserId.of(request.recordedBy())));
        return presentMedicalHistory(h);
    }

    public List<MedicalHistoryResponse> getPatientMedicalHistory(String patientId) {
        return service.getPatientMedicalHistory(PatientId.of(patientId)).stream().map(this::presentMedicalHistory).toList();
    }

    public List<EncounterResponse> getPatientEncounters(String patientId) {
        return service.getPatientEncounters(PatientId.of(patientId)).stream().map(this::presentEncounter).toList();
    }

    public EncounterResponse getEncounter(String id) {
        return service.getEncounter(EncounterId.of(id)).map(this::presentEncounter).orElse(null);
    }

    private EncounterResponse presentEncounter(Encounter e) {
        return new EncounterResponse(e.id().value().toString(), e.appointmentId().value().toString(),
                e.patientId().value().toString(), e.doctorId().value().toString(),
                e.occurredAt(), e.reason(), e.diagnosis(), e.notes(), e.patientVisible());
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