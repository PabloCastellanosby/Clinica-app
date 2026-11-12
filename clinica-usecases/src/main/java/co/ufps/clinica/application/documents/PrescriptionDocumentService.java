package co.ufps.clinica.application.documents;

import co.ufps.clinica.application.clinicalrecord.ClinicalRecordService;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionId;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.UserId;
import java.util.Objects;

public final class PrescriptionDocumentService {
    private final ClinicalRecordService clinicalRecords;
    private final DocumentGenerator generator;
    public PrescriptionDocumentService(ClinicalRecordService clinicalRecords, DocumentGenerator generator) {
        this.clinicalRecords = Objects.requireNonNull(clinicalRecords);
        this.generator = Objects.requireNonNull(generator);
    }
    public byte[] pdfForUser(PrescriptionId id, UserId userId, Role role) {
        return generator.renderPrescriptionPdf(clinicalRecords.prescriptionForUser(id, userId, role));
    }
}
