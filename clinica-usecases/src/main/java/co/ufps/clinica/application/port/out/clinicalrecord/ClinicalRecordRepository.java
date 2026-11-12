package co.ufps.clinica.application.port.out.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.appointment.AppointmentId;
import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository {
    List<Encounter> findByPatient(PatientId patientId);
    Optional<Encounter> findByAppointment(AppointmentId appointmentId);
    Optional<Encounter> findById(EncounterId id);
    Encounter save(Encounter encounter);
    VitalSigns saveVitalSigns(VitalSigns vitalSigns);
    Prescription savePrescription(Prescription prescription);
    ExamOrder saveExamOrder(ExamOrder examOrder);
    Optional<ExamOrder> findExamOrder(ExamOrderId id);
    List<ExamOrder> findExamOrdersByEncounter(EncounterId encounterId);
    Allergy saveAllergy(Allergy allergy);
    List<Allergy> findAllergies(PatientId patientId);
    MedicalHistory saveMedicalHistory(MedicalHistory history);
    List<MedicalHistory> findMedicalHistory(PatientId patientId);
    Attachment saveAttachment(Attachment attachment);
    Optional<Prescription> findPrescription(PrescriptionId id);
    Optional<Attachment> findAttachment(AttachmentId id);
    List<Attachment> findAttachmentsByEncounter(EncounterId encounterId);
}
