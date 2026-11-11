package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.util.List;

public record IssuePrescriptionCommand(EncounterId encounterId, PatientId patientId, DoctorId doctorId,
                                       String instructions, List<PrescriptionLine> lines) {}
