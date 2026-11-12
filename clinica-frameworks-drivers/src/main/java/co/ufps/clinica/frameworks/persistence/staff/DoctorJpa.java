package co.ufps.clinica.frameworks.persistence.staff;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "DOCTORS", indexes = {
        @Index(name = "idx_doctor_license", columnList = "license_number"),
        @Index(name = "idx_doctor_specialty", columnList = "specialty_id")
})
public class DoctorJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_doctors_user"))
    private co.ufps.clinica.frameworks.persistence.identity.UserJpa userReference;
    @Column(name = "license_number", length = 50, nullable = false, unique = true)
    private String licenseNumber;
    @Column(name = "first_name", length = 120, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 120, nullable = false)
    private String lastName;
    @Column(name = "specialty_id", nullable = false)
    private UUID specialtyId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "specialty_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_doctors_specialty"))
    private co.ufps.clinica.frameworks.persistence.catalog.SpecialtyJpa specialtyReference;
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    public DoctorJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public UUID getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(UUID specialtyId) { this.specialtyId = specialtyId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
