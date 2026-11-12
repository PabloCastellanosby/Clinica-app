package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.staff.StaffStatus;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.appointment.AppointmentStatus;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import java.time.Instant;
import java.util.*;

public final class ClinicalRecordService {
    private final ClinicalRecordRepository repository;
    private final AppointmentRepository appointments;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private static final int MAX_ATTACHMENT_BYTES = 5 * 1024 * 1024;
    public ClinicalRecordService(ClinicalRecordRepository repository, AppointmentRepository appointments,
                                 DoctorRepository doctors, PatientRepository patients) {
        this.repository = Objects.requireNonNull(repository);
        this.appointments = Objects.requireNonNull(appointments);
        this.doctors = Objects.requireNonNull(doctors);
        this.patients = Objects.requireNonNull(patients);
    }

    public Encounter createEncounter(CreateEncounterCommand command, UserId actorId, Role actorRole) {
        if (command == null || command.appointmentId() == null || command.patientId() == null || command.doctorId() == null)
            throw new IllegalArgumentException("La historia debe indicar cita, paciente y médico");
        var appointment = appointments.findById(command.appointmentId())
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        if (appointment.status() != AppointmentStatus.IN_CONSULTATION && appointment.status() != AppointmentStatus.COMPLETED)
            throw new IllegalStateException("Solo se puede abrir historia para una cita en consulta o completada");
        if (!appointment.patientId().equals(command.patientId()) || !appointment.doctorId().equals(command.doctorId()))
            throw new SecurityException("Paciente o médico no coincide con la cita");
        if (actorRole == Role.MEDICO) {
            var doctor = doctors.findByUserId(actorId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un médico"));
            if (doctor.status() != StaffStatus.ACTIVE) throw new SecurityException("El médico está inactivo");
            if (!doctor.id().equals(appointment.doctorId())) throw new SecurityException("El médico solo puede abrir historias de sus propias citas");
        } else if (actorRole != Role.ADMINISTRADOR) {
            throw new SecurityException("No tiene permiso para crear una historia clínica");
        }
        if (repository.findByAppointment(appointment.id()).isPresent())
            throw new IllegalStateException("La cita ya tiene un encuentro clínico registrado");
        Encounter encounter = new Encounter(EncounterId.generate(), appointment.id(), appointment.patientId(),
                appointment.doctorId(), Instant.now(), command.reason(), command.diagnosis(), command.notes(), command.patientVisible());
        return repository.save(encounter);
    }

    public VitalSigns recordVitalSigns(VitalSignsRecordCommand command, UserId actorId, Role role) {
        if (command == null || command.encounterId() == null)
            throw new IllegalArgumentException("Los signos vitales deben indicar el encuentro clínico");
        requireWritableEncounter(command.encounterId(), actorId, role);
        VitalSigns vitalSigns = new VitalSigns(VitalSignsId.generate(), command.encounterId(),
                command.temperatureC(), command.systolic(), command.diastolic(), command.heartRate(),
                command.respiratoryRate(), command.weightKg(), command.heightCm(), Instant.now());
        return repository.saveVitalSigns(vitalSigns);
    }

    public Prescription issuePrescription(IssuePrescriptionCommand command, UserId actorId, Role role) {
        if (command == null || command.encounterId() == null || command.patientId() == null
                || command.doctorId() == null || command.lines() == null || command.lines().isEmpty())
            throw new IllegalArgumentException("La receta debe incluir encuentro, paciente, médico y medicamentos");
        Encounter encounter = requireWritableEncounter(command.encounterId(), actorId, role);
        if (!encounter.patientId().equals(command.patientId()) || !encounter.doctorId().equals(command.doctorId()))
            throw new SecurityException("El paciente o médico de la receta no coincide con el encuentro");
        Prescription prescription = new Prescription(PrescriptionId.generate(), command.encounterId(),
                encounter.patientId(), encounter.doctorId(), Instant.now(), command.instructions(), command.lines());
        return repository.savePrescription(prescription);
    }

    public ExamOrder orderExam(OrderExamCommand command, UserId actorId, Role role) {
        if (command == null || command.encounterId() == null || command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("La orden debe indicar encuentro y nombre de examen");
        requireWritableEncounter(command.encounterId(), actorId, role);
        ExamOrder examOrder = new ExamOrder(ExamOrderId.generate(), command.encounterId(),
                command.name(), command.instructions(), ExamOrderStatus.ORDERED, Instant.now(), null, null);
        return repository.saveExamOrder(examOrder);
    }

    public ExamOrder recordExamResult(ExamOrderId orderId, String result, UserId actorId, Role role) {
        if (orderId == null) throw new IllegalArgumentException("La orden de examen es obligatoria");
        ExamOrder order = repository.findExamOrder(orderId)
                .orElseThrow(() -> new NoSuchElementException("Orden de examen no encontrada"));
        requireWritableEncounter(order.encounterId(), actorId, role);
        return repository.saveExamOrder(order.complete(result, Instant.now()));
    }

    public List<ExamOrder> examOrdersForEncounter(EncounterId encounterId, UserId actorId, Role role) {
        Encounter encounter = repository.findById(encounterId)
                .orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeEncounterRead(encounter, actorId, role);
        return repository.findExamOrdersByEncounter(encounterId);
    }

    public Allergy registerAllergy(RegisterAllergyCommand command, UserId actorId, Role role) {
        if (command == null || command.patientId() == null)
            throw new IllegalArgumentException("La alergia debe indicar el paciente");
        authorizeClinicalPatient(command.patientId(), actorId, role);
        Allergy allergy = new Allergy(AllergyId.generate(), command.patientId(),
                command.substance(), command.reaction(), command.severity());
        return repository.saveAllergy(allergy);
    }

    public List<Allergy> getPatientAllergies(PatientId patientId, UserId actorId, Role role) {
        authorizeClinicalPatient(patientId, actorId, role);
        return repository.findAllergies(patientId);
    }

    public MedicalHistory addMedicalHistory(AddMedicalHistoryCommand command, UserId actorId, Role role) {
        if (command == null || command.patientId() == null || actorId == null)
            throw new IllegalArgumentException("El antecedente debe indicar paciente y responsable");
        authorizeClinicalPatient(command.patientId(), actorId, role);
        MedicalHistory history = new MedicalHistory(MedicalHistoryId.generate(), command.patientId(),
                command.type(), command.description(), Instant.now(), actorId);
        return repository.saveMedicalHistory(history);
    }

    public List<MedicalHistory> getPatientMedicalHistory(PatientId patientId, UserId actorId, Role role) {
        authorizeClinicalPatient(patientId, actorId, role);
        return repository.findMedicalHistory(patientId);
    }

    public List<Encounter> getPatientEncounters(PatientId patientId, UserId actorId, Role role) {
        authorizeClinicalPatient(patientId, actorId, role);
        List<Encounter> encounters = repository.findByPatient(patientId);
        return role == Role.PACIENTE
                ? encounters.stream().filter(Encounter::patientVisible).toList()
                : encounters;
    }

    public Encounter encounterForUser(EncounterId id, UserId actorId, Role role) {
        Encounter encounter = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeEncounterRead(encounter, actorId, role);
        return encounter;
    }

    public Prescription prescriptionForUser(PrescriptionId id, UserId actorId, Role role) {
        Prescription prescription = repository.findPrescription(id).orElseThrow(() -> new NoSuchElementException("Receta no encontrada"));
        Encounter encounter = repository.findById(prescription.encounterId())
                .orElseThrow(() -> new IllegalStateException("La receta no está vinculada a un encuentro existente"));
        if (!encounter.patientId().equals(prescription.patientId()) || !encounter.doctorId().equals(prescription.doctorId()))
            throw new IllegalStateException("La receta y el encuentro tienen datos inconsistentes");
        authorizeEncounterRead(encounter, actorId, role);
        return prescription;
    }

    public Attachment addAttachment(EncounterId encounterId, String fileName, String contentType, byte[] content,
                                    UserId actorId, Role role) {
        if (role != Role.ADMINISTRADOR && role != Role.MEDICO) throw new SecurityException("Solo personal clínico puede adjuntar archivos");
        Encounter encounter = repository.findById(encounterId).orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeDoctorForEncounter(encounter, actorId, role);
        if (content == null || content.length == 0 || content.length > MAX_ATTACHMENT_BYTES)
            throw new IllegalArgumentException("El archivo debe tener entre 1 byte y 5 MB");
        String normalizedType = contentType == null ? "application/octet-stream" : contentType.trim().toLowerCase(Locale.ROOT);
        if (!Set.of("application/pdf", "image/jpeg", "image/png", "text/plain").contains(normalizedType))
            throw new IllegalArgumentException("Tipo de archivo no permitido");
        if (fileName == null || fileName.isBlank() || fileName.length() > 250 || fileName.contains("/") || fileName.contains("\\")
                || fileName.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Nombre de archivo no válido");
        Attachment attachment = new Attachment(AttachmentId.generate(), encounterId, fileName.trim(), normalizedType,
                null, content.length, Instant.now(), content);
        return repository.saveAttachment(attachment);
    }

    public Attachment attachmentForUser(AttachmentId id, UserId actorId, Role role) {
        Attachment attachment = repository.findAttachment(id).orElseThrow(() -> new NoSuchElementException("Adjunto no encontrado"));
        Encounter encounter = repository.findById(attachment.encounterId()).orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeEncounterRead(encounter, actorId, role);
        return attachment;
    }

    public List<Attachment> attachmentsForEncounter(EncounterId id, UserId actorId, Role role) {
        Encounter encounter = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeEncounterRead(encounter, actorId, role);
        return repository.findAttachmentsByEncounter(id);
    }

    private void authorizeDoctorForEncounter(Encounter encounter, UserId actorId, Role role) {
        if (role == Role.ADMINISTRADOR) return;
        var doctor = doctors.findByUserId(actorId).orElseThrow(() -> new SecurityException("Cuenta médica sin profesional asociado"));
        if (doctor.status() != StaffStatus.ACTIVE) throw new SecurityException("El médico está inactivo");
        if (!doctor.id().equals(encounter.doctorId())) throw new SecurityException("El médico solo puede modificar sus propios encuentros");
    }

    private void authorizeEncounterRead(Encounter encounter, UserId actorId, Role role) {
        if (role == Role.ADMINISTRADOR) return;
        if (role == Role.MEDICO) { authorizeDoctorForEncounter(encounter, actorId, role); return; }
        if (role == Role.PACIENTE) {
            var patient = patients.findByUserId(actorId).orElseThrow(() -> new SecurityException("Cuenta sin perfil de paciente asociado"));
            if (patient.id().equals(encounter.patientId()) && encounter.patientVisible()) return;
        }
        throw new SecurityException("No tiene acceso a este encuentro clínico");
    }

    private Encounter requireWritableEncounter(EncounterId id, UserId actorId, Role role) {
        if (role != Role.ADMINISTRADOR && role != Role.MEDICO) throw new SecurityException("Solo el personal clínico puede modificar el encuentro");
        Encounter encounter = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Encuentro clínico no encontrado"));
        authorizeDoctorForEncounter(encounter, actorId, role);
        return encounter;
    }

    private void authorizeClinicalPatient(PatientId patientId, UserId actorId, Role role) {
        if (role == Role.ADMINISTRADOR) return;
        if (role == Role.PACIENTE) {
            var patient = patients.findByUserId(actorId).orElseThrow(() -> new SecurityException("Cuenta sin perfil de paciente asociado"));
            if (patient.id().equals(patientId)) return;
        }
        if (role == Role.MEDICO) {
            var doctor = doctors.findByUserId(actorId).orElseThrow(() -> new SecurityException("Cuenta médica sin profesional asociado"));
            if (doctor.status() != StaffStatus.ACTIVE) throw new SecurityException("El médico está inactivo");
            if (appointments.hasClinicalRelationship(doctor.id(), patientId)) return;
        }
        throw new SecurityException("No tiene acceso a la historia clínica de este paciente");
    }
}
