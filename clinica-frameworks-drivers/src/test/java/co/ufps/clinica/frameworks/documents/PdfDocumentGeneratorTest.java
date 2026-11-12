package co.ufps.clinica.frameworks.documents;

import static org.junit.jupiter.api.Assertions.assertTrue;

import co.ufps.clinica.domain.clinicalrecord.Prescription;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionId;
import co.ufps.clinica.domain.clinicalrecord.EncounterId;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionLine;
import co.ufps.clinica.domain.clinicalrecord.PrescriptionLineId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PdfDocumentGeneratorTest {
    @Test void rendersValidPdfObjectsAndSplitsLongPrescriptionsAcrossPages() {
        List<PrescriptionLine> lines = java.util.stream.IntStream.range(0, 50)
                .mapToObj(i -> new PrescriptionLine(PrescriptionLineId.generate(), "Medicamento " + i,
                        "tableta", "500 mg", "cada 8 horas", 5, 15)).toList();
        Prescription prescription = new Prescription(PrescriptionId.generate(), EncounterId.generate(),
                PatientId.of(UUID.randomUUID()), DoctorId.of(UUID.randomUUID()), Instant.now(),
                "Tomar con agua (no triturar)", lines);

        String pdf = new String(new PdfDocumentGenerator().renderPrescriptionPdf(prescription), StandardCharsets.US_ASCII);

        assertTrue(pdf.startsWith("%PDF-1.4"));
        assertTrue(pdf.contains("/Count 2"));
        assertTrue(pdf.contains("Instrucciones: Tomar con agua \\(no triturar\\)"));
        assertTrue(pdf.endsWith("%%EOF\n"));
    }
}
