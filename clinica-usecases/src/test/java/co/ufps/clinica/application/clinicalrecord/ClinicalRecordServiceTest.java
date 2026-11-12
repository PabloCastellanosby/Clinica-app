package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.appointment.AppointmentStatus;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinicalrecord.Encounter;
import co.ufps.clinica.domain.clinicalrecord.EncounterId;
import co.ufps.clinica.domain.clinicalrecord.Prescription;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionId;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionLine;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionLineId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.staff.DoctorId;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClinicalRecordServiceTest {
    @Test
    void patientCannotSeeEncountersMarkedAsNotPatientVisible() {
        UserId userId = UserId.generate();
        Patient patient = Patient.register("CC", "123", "Ana", "Pérez", null,
                "ana@example.com", null, null, null).linkedToAccount(userId);
        DoctorId doctorId = DoctorId.generate();
        Encounter visible = encounter(patient.id(), doctorId, true);
        Encounter hidden = encounter(patient.id(), doctorId, false);
        ClinicalRecordService service = clinicalService(
                method -> method.equals("findByPatient") ? List.of(visible, hidden) : defaultValue(method),
                method -> method.equals("findByUserId") ? Optional.of(patient) : defaultValue(method));

        assertEquals(List.of(visible), service.getPatientEncounters(patient.id(), userId, Role.PACIENTE));
    }

    @Test
    void patientCannotDownloadPrescriptionForHiddenEncounter() {
        UserId userId = UserId.generate();
        Patient patient = Patient.register("CC", "456", "Luis", "Díaz", null,
                "luis@example.com", null, null, null).linkedToAccount(userId);
        DoctorId doctorId = DoctorId.generate();
        Encounter hidden = encounter(patient.id(), doctorId, false);
        Prescription prescription = new Prescription(PrescriptionId.generate(), hidden.id(), patient.id(), doctorId,
                Instant.now(), null, List.of(new PrescriptionLine(PrescriptionLineId.generate(), "Medicamento", null,
                "1 tableta", "cada 8 horas", 5, 15)));
        ClinicalRecordService service = clinicalService(
                method -> switch (method) {
                    case "findPrescription" -> Optional.of(prescription);
                    case "findById" -> Optional.of(hidden);
                    default -> defaultValue(method);
                },
                method -> method.equals("findByUserId") ? Optional.of(patient) : defaultValue(method));

        assertThrows(SecurityException.class, () -> service.prescriptionForUser(
                prescription.id(), userId, Role.PACIENTE));
    }

    @Test
    void refusesToCreateSecondEncounterForSameAppointment() {
        PatientId patientId = PatientId.generate();
        DoctorId doctorId = DoctorId.generate();
        Appointment appointment = Appointment.schedule(patientId, doctorId, BranchId.generate(), OfficeId.generate(),
                null, Instant.now().plusSeconds(60), Instant.now().plusSeconds(1800), Instant.now())
                .withStatus(AppointmentStatus.CONFIRMED)
                .withStatus(AppointmentStatus.CHECKED_IN)
                .withStatus(AppointmentStatus.IN_CONSULTATION);
        ClinicalRecordService service = new ClinicalRecordService(
                proxy(ClinicalRecordRepository.class, method -> method.equals("findByAppointment")
                        ? Optional.of(new co.ufps.clinica.domain.clinicalrecord.Encounter(
                                co.ufps.clinica.domain.clinicalrecord.EncounterId.generate(), appointment.id(), patientId,
                                doctorId, Instant.now(), null, null, null, false)) : defaultValue(method)),
                proxy(AppointmentRepository.class, method -> method.equals("findById") ? Optional.of(appointment) : defaultValue(method)),
                proxy(DoctorRepository.class, method -> defaultValue(method)),
                proxy(PatientRepository.class, method -> defaultValue(method)));

        assertThrows(IllegalStateException.class, () -> service.createEncounter(
                new CreateEncounterCommand(appointment.id(), patientId, doctorId, null, null, null, false),
                co.ufps.clinica.domain.identity.UserId.generate(), Role.ADMINISTRADOR));
    }

    private static <T> T proxy(Class<T> type, java.util.function.Function<String, Object> resultByMethod) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (instance, method, arguments) -> resultByMethod.apply(method.getName())));
    }

    private static ClinicalRecordService clinicalService(
            java.util.function.Function<String, Object> clinicalResults,
            java.util.function.Function<String, Object> patientResults) {
        return new ClinicalRecordService(proxy(ClinicalRecordRepository.class, clinicalResults),
                proxy(AppointmentRepository.class, method -> defaultValue(method)),
                proxy(DoctorRepository.class, method -> defaultValue(method)),
                proxy(PatientRepository.class, patientResults));
    }

    private static Encounter encounter(PatientId patientId, DoctorId doctorId, boolean patientVisible) {
        return new Encounter(EncounterId.generate(), AppointmentId.generate(), patientId, doctorId,
                Instant.now(), null, null, null, patientVisible);
    }

    private static Object defaultValue(String method) {
        return switch (method) {
            case "findById", "findByUserId", "findByAppointment", "findByEmail", "findByDocument",
                    "findPrescription", "findAttachment" -> Optional.empty();
            case "findByPatient", "findForDoctor", "findForPatient", "findByDoctor", "findAllActive",
                    "findAllActiveSpecialties", "findAllActiveServices", "findAllergies", "findMedicalHistory",
                    "findAttachmentsByEncounter", "search" -> List.of();
            case "hasConflict", "hasClinicalRelationship" -> false;
            default -> null;
        };
    }
}
