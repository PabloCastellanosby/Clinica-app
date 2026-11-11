package co.ufps.clinica.application.port.out.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository {
    List<Encounter> findByPatient(PatientId patientId);
    Optional<Encounter> findById(EncounterId id);
    Encounter save(Encounter encounter);
    VitalSigns saveVitalSigns(VitalSigns vitalSigns);
    Prescription savePrescription(Prescription prescription);
    ExamOrder saveExamOrder(ExamOrder examOrder);
    Allergy saveAllergy(Allergy allergy);
    List<Allergy> findAllergies(PatientId patientId);
    MedicalHistory saveMedicalHistory(MedicalHistory history);
    List<MedicalHistory> findMedicalHistory(PatientId patientId);
    Attachment saveAttachment(Attachment attachment);
}
