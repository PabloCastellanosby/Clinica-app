package co.ufps.clinica.application.documents;
import co.ufps.clinica.domain.clinicalrecord.Prescription;
public interface DocumentGenerator {
    byte[] renderPrescriptionPdf(Prescription prescription);
}
