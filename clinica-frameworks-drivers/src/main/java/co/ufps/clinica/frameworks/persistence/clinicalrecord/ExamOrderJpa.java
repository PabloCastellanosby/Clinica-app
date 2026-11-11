package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "exam_orders")
public class ExamOrderJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;
    @Column(name = "name", length = 200, nullable = false)
    private String name;
    @Column(name = "instructions", length = 2000)
    private String instructions;
    @Column(name = "status", length = 20, nullable = false)
    private String status;
    @Column(name = "ordered_at", nullable = false)
    private Instant orderedAt;
    @Column(name = "completed_at")
    private Instant completedAt;
    @Column(name = "result", length = 5000)
    private String result;

    public ExamOrderJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEncounterId() { return encounterId; }
    public void setEncounterId(UUID encounterId) { this.encounterId = encounterId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getOrderedAt() { return orderedAt; }
    public void setOrderedAt(Instant orderedAt) { this.orderedAt = orderedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
