package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
import java.util.*;

public final class ClinicalRecordService {
    private final ClinicalRecordRepository repository;
    public ClinicalRecordService(ClinicalRecordRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Encounter createEncounter(CreateEncounterCommand command) {
        Encounter encounter = new Encounter(EncounterId.generate(), command.appointmentId(), command.patientId(),
                command.doctorId(), Instant.now(), command.reason(), command.diagnosis(), command.notes(), command.patientVisible());
        return repository.save(encounter);
    }

    public VitalSigns recordVitalSigns(VitalSignsRecordCommand command) {
        VitalSigns vitalSigns = new VitalSigns(VitalSignsId.generate(), command.encounterId(),
                command.temperatureC(), command.systolic(), command.diastolic(), command.heartRate(),
                command.respiratoryRate(), command.weightKg(), command.heightCm(), Instant.now());
        return repository.saveVitalSigns(vitalSigns);
    }

    public Prescription issuePrescription(IssuePrescriptionCommand command) {
        Prescription prescription = new Prescription(PrescriptionId.generate(), command.encounterId(),
                command.patientId(), command.doctorId(), Instant.now(), command.instructions(), command.lines());
        return repository.savePrescription(prescription);
    }

    public ExamOrder orderExam(OrderExamCommand command) {
        ExamOrder examOrder = new ExamOrder(ExamOrderId.generate(), command.encounterId(),
                command.name(), command.instructions(), ExamOrderStatus.ORDERED, Instant.now(), null, null);
        return repository.saveExamOrder(examOrder);
    }

    public Allergy registerAllergy(RegisterAllergyCommand command) {
        Allergy allergy = new Allergy(AllergyId.generate(), command.patientId(),
                command.substance(), command.reaction(), command.severity());
        return repository.saveAllergy(allergy);
    }

    public List<Allergy> getPatientAllergies(PatientId patientId) {
        return repository.findAllergies(patientId);
    }

    public MedicalHistory addMedicalHistory(AddMedicalHistoryCommand command) {
        MedicalHistory history = new MedicalHistory(MedicalHistoryId.generate(), command.patientId(),
                command.type(), command.description(), Instant.now(), command.recordedBy());
        return repository.saveMedicalHistory(history);
    }

    public List<MedicalHistory> getPatientMedicalHistory(PatientId patientId) {
        return repository.findMedicalHistory(patientId);
    }

    public List<Encounter> getPatientEncounters(PatientId patientId) {
        return repository.findByPatient(patientId);
    }

    public Optional<Encounter> getEncounter(EncounterId id) {
        return repository.findById(id);
    }
}
