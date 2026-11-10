package co.ufps.clinica.application.documents;
import co.ufps.clinica.domain.clinicalrecord.Prescription;
import java.util.Map;
public interface DocumentGenerator {
    byte[] renderPrescriptionPdf(Prescription prescription);
    byte[] exportReport(String reportType, Map<String, String> filters);
}
