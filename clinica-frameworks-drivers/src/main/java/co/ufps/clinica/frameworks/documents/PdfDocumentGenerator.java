package co.ufps.clinica.frameworks.documents;

import co.ufps.clinica.application.documents.DocumentGenerator;
import co.ufps.clinica.domain.clinicalrecord.Prescription;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PdfDocumentGenerator implements DocumentGenerator {
    private static final int LINES_PER_PAGE = 48;

    @Override public byte[] renderPrescriptionPdf(Prescription prescription) {
        List<String> lines = new ArrayList<>();
        lines.add("CLINICA - RECETA MEDICA");
        lines.add("Receta: " + prescription.id().value());
        lines.add("Paciente: " + prescription.patientId().value());
        lines.add("Medico: " + prescription.doctorId().value());
        lines.add("Fecha: " + DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(prescription.issuedAt()));
        appendWrapped(lines, "Instrucciones: " + safe(prescription.instructions()));
        lines.add("");
        lines.add("Medicamentos:");
        prescription.lines().forEach(line -> appendWrapped(lines, String.format("- %s | %s | dosis %s | frecuencia %s | %d dias | cantidad %d",
                safe(line.medicine()), safe(line.presentation()), safe(line.dose()), safe(line.frequency()), line.durationDays(), line.quantity())));
        if (prescription.lines().isEmpty()) lines.add("No hay medicamentos registrados.");
        return pdf(lines);
    }

    private byte[] pdf(List<String> lines) {
        List<List<String>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE)
            pages.add(lines.subList(i, Math.min(i + LINES_PER_PAGE, lines.size())));
        int fontId = 3;
        int pageStartId = 4;
        int objectCount = 3 + pages.size() * 2;
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++) kids.append(pageStartId + i * 2).append(" 0 R ");
        List<byte[]> objects = new ArrayList<>();
        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.add(ascii("<< /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >>"));
        objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        for (int i = 0; i < pages.size(); i++) {
            int pageId = pageStartId + i * 2;
            int streamId = pageId + 1;
            StringBuilder text = new StringBuilder("BT /F1 10 Tf 50 750 Td 14 TL\n");
            for (String line : pages.get(i)) text.append('(').append(pdfEscape(safe(line))).append(") Tj T*\n");
            text.append("ET\n");
            byte[] stream = ascii(text.toString());
            objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 "
                    + fontId + " 0 R >> >> /Contents " + streamId + " 0 R >>"));
            ByteArrayOutputStream object = new ByteArrayOutputStream();
            write(object, ascii("<< /Length " + stream.length + " >>\nstream\n"));
            write(object, stream);
            write(object, ascii("endstream"));
            objects.add(object.toByteArray());
        }
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        write(document, ascii("%PDF-1.4\n"));
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(document.size());
            write(document, ascii((i + 1) + " 0 obj\n"));
            write(document, objects.get(i));
            write(document, ascii("\nendobj\n"));
        }
        int xref = document.size();
        write(document, ascii("xref\n0 " + (objectCount + 1) + "\n0000000000 65535 f \n"));
        for (int i = 1; i < offsets.size(); i++) write(document, ascii(String.format("%010d 00000 n \n", offsets.get(i))));
        write(document, ascii("trailer\n<< /Size " + (objectCount + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n"));
        return document.toByteArray();
    }
    private String safe(String value) {
        if (value == null) return "";
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^\\x20-\\x7E]", "?");
    }
    private void appendWrapped(List<String> target, String value) {
        String remaining = value;
        while (remaining.length() > 90) {
            int split = remaining.lastIndexOf(' ', 90);
            if (split < 1) split = 90;
            target.add(remaining.substring(0, split));
            remaining = remaining.substring(split).stripLeading();
        }
        target.add(remaining);
    }
    private String pdfEscape(String value) { return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"); }
    private byte[] ascii(String text) { return text.getBytes(StandardCharsets.US_ASCII); }
    private void write(ByteArrayOutputStream output, byte[] bytes) { output.write(bytes, 0, bytes.length); }
}
