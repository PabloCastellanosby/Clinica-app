package co.ufps.clinica.frameworks.persistence.billing;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "INVOICES", indexes = {
        @Index(name = "idx_invoice_patient", columnList = "patient_id"),
        @Index(name = "idx_invoice_number", columnList = "number")
})
public class InvoiceJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "patient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_invoices_patient"))
    private co.ufps.clinica.frameworks.persistence.patient.PatientJpa patientReference;
    @Column(name = "appointment_id")
    private UUID appointmentId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "appointment_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_invoices_appointment"))
    private co.ufps.clinica.frameworks.persistence.appointment.AppointmentJpa appointmentReference;
    @Column(name = "number", length = 50, nullable = false, unique = true)
    private String number;
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;
    @Column(name = "subtotal", nullable = false)
    private long subtotal;
    @Column(name = "tax", nullable = false)
    private long tax;
    @Column(name = "total", nullable = false)
    private long total;
    @Column(name = "currency", length = 10, nullable = false)
    private String currency;
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    public InvoiceJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    public long getSubtotal() { return subtotal; }
    public void setSubtotal(long subtotal) { this.subtotal = subtotal; }
    public long getTax() { return tax; }
    public void setTax(long tax) { this.tax = tax; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
