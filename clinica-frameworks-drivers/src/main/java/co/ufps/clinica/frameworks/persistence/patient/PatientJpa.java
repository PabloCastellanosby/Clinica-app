package co.ufps.clinica.frameworks.persistence.patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "PATIENTS",
       uniqueConstraints = @UniqueConstraint(name = "uq_patient_document", columnNames = {"document_type", "document_number"}),
       indexes = @Index(name = "idx_patient_name", columnList = "last_name, first_name"))
public class PatientJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "document_type", length = 40, nullable = false)
    private String documentType;
    @Column(name = "document_number", length = 80, nullable = false)
    private String documentNumber;
    @Column(name = "first_name", length = 120, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 120, nullable = false)
    private String lastName;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "email", length = 254)
    private String email;
    @Column(name = "phone", length = 40)
    private String phone;
    @Column(name = "address", length = 500)
    private String address;
    @Column(name = "affiliation_type", length = 80)
    private String affiliationType;
    @Column(name = "user_id", unique = true)
    private UUID userId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_patients_user"))
    private co.ufps.clinica.frameworks.persistence.identity.UserJpa userReference;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public PatientJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAffiliationType() { return affiliationType; }
    public void setAffiliationType(String affiliationType) { this.affiliationType = affiliationType; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
