package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClinicalRecordRepositoryJpa implements ClinicalRecordRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Encounter save(Encounter encounter) {
        entityManager.merge(toJpa(encounter)); return encounter;
    }
    @Override public Optional<Encounter> findById(EncounterId id) {
        EncounterJpa jpa = entityManager.find(EncounterJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }
    @Override public List<Encounter> findByPatient(PatientId patientId) {
        return entityManager.createQuery("SELECT e FROM EncounterJpa e WHERE e.patientId = :pid", EncounterJpa.class)
                .setParameter("pid", patientId.value()).getResultList().stream().map(this::toDomain).toList();
    }
    @Override @Transactional public VitalSigns saveVitalSigns(VitalSigns vitalSigns) {
        entityManager.merge(toJpa(vitalSigns)); return vitalSigns;
    }
    @Override @Transactional public Prescription savePrescription(Prescription prescription) {
        entityManager.merge(toJpa(prescription)); return prescription;
    }
    @Override @Transactional public ExamOrder saveExamOrder(ExamOrder examOrder) {
        entityManager.merge(toJpa(examOrder)); return examOrder;
    }
    @Override @Transactional public Allergy saveAllergy(Allergy allergy) {
        entityManager.merge(toJpa(allergy)); return allergy;
    }
    @Override public List<Allergy> findAllergies(PatientId patientId) {
        return entityManager.createQuery("SELECT a FROM AllergyJpa a WHERE a.patientId = :pid", AllergyJpa.class)
                .setParameter("pid", patientId.value()).getResultList().stream().map(this::toDomain).toList();
    }
    @Override @Transactional public MedicalHistory saveMedicalHistory(MedicalHistory history) {
        entityManager.merge(toJpa(history)); return history;
    }
    @Override public List<MedicalHistory> findMedicalHistory(PatientId patientId) {
        return entityManager.createQuery("SELECT m FROM MedicalHistoryJpa m WHERE m.patientId = :pid", MedicalHistoryJpa.class)
                .setParameter("pid", patientId.value()).getResultList().stream().map(this::toDomain).toList();
    }
    private EncounterJpa toJpa(Encounter e) {
        EncounterJpa jpa = new EncounterJpa();
        jpa.setId(e.id().value()); jpa.setAppointmentId(e.appointmentId().value());
        jpa.setPatientId(e.patientId().value()); jpa.setDoctorId(e.doctorId().value());
        jpa.setOccurredAt(e.occurredAt()); jpa.setReason(e.reason());
        jpa.setDiagnosis(e.diagnosis()); jpa.setNotes(e.notes()); jpa.setPatientVisible(e.patientVisible());
        return jpa;
    }
    private Encounter toDomain(EncounterJpa jpa) {
        return new Encounter(EncounterId.of(jpa.getId()), AppointmentId.of(jpa.getAppointmentId()),
                PatientId.of(jpa.getPatientId()), DoctorId.of(jpa.getDoctorId()),
                jpa.getOccurredAt(), jpa.getReason(), jpa.getDiagnosis(), jpa.getNotes(), jpa.isPatientVisible());
    }
    private VitalSignsJpa toJpa(VitalSigns v) {
        VitalSignsJpa jpa = new VitalSignsJpa();
        jpa.setId(v.id().value()); jpa.setEncounterId(v.encounterId().value());
        jpa.setTemperatureC(v.temperatureC()); jpa.setSystolic(v.systolic());
        jpa.setDiastolic(v.diastolic()); jpa.setHeartRate(v.heartRate());
        jpa.setRespiratoryRate(v.respiratoryRate()); jpa.setWeightKg(v.weightKg());
        jpa.setHeightCm(v.heightCm()); jpa.setMeasuredAt(v.measuredAt());
        return jpa;
    }
    private PrescriptionJpa toJpa(Prescription p) {
        PrescriptionJpa jpa = new PrescriptionJpa();
        jpa.setId(p.id().value()); jpa.setEncounterId(p.encounterId().value());
        jpa.setPatientId(p.patientId().value()); jpa.setDoctorId(p.doctorId().value());
        jpa.setIssuedAt(p.issuedAt()); jpa.setInstructions(p.instructions());
        return jpa;
    }
    private ExamOrderJpa toJpa(ExamOrder e) {
        ExamOrderJpa jpa = new ExamOrderJpa();
        jpa.setId(e.id().value()); jpa.setEncounterId(e.encounterId().value());
        jpa.setName(e.name()); jpa.setInstructions(e.instructions());
        jpa.setStatus(e.status().name()); jpa.setOrderedAt(e.orderedAt());
        jpa.setCompletedAt(e.completedAt()); jpa.setResult(e.result());
        return jpa;
    }
    private AllergyJpa toJpa(Allergy a) {
        AllergyJpa jpa = new AllergyJpa();
        jpa.setId(a.id().value()); jpa.setPatientId(a.patientId().value());
        jpa.setSubstance(a.substance()); jpa.setReaction(a.reaction());
        jpa.setSeverity(a.severity());
        return jpa;
    }
    private Allergy toDomain(AllergyJpa jpa) {
        return new Allergy(AllergyId.of(jpa.getId()), PatientId.of(jpa.getPatientId()),
                jpa.getSubstance(), jpa.getReaction(), jpa.getSeverity());
    }
    private MedicalHistoryJpa toJpa(MedicalHistory m) {
        MedicalHistoryJpa jpa = new MedicalHistoryJpa();
        jpa.setId(m.id().value()); jpa.setPatientId(m.patientId().value());
        jpa.setType(m.type()); jpa.setDescription(m.description());
        jpa.setRecordedAt(m.recordedAt()); jpa.setRecordedBy(m.recordedBy().value());
        return jpa;
    }
    private MedicalHistory toDomain(MedicalHistoryJpa jpa) {
        return new MedicalHistory(MedicalHistoryId.of(jpa.getId()), PatientId.of(jpa.getPatientId()),
                jpa.getType(), jpa.getDescription(), jpa.getRecordedAt(), UserId.of(jpa.getRecordedBy()));
    }
}
