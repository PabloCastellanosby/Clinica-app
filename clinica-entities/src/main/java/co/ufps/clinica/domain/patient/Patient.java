package co.ufps.clinica.domain.patient;

import java.time.Instant;
import java.time.LocalDate;

public record Patient(PatientId id, String documentType, String documentNumber, String firstName, String lastName,
                      LocalDate birthDate, String email, String phone, String address, String affiliationType,
                      Instant createdAt) {
    public static Patient register(String documentType, String documentNumber, String firstName, String lastName,
                                   LocalDate birthDate, String email, String phone, String address, String affiliationType) {
        return new Patient(PatientId.generate(), documentType, documentNumber, firstName, lastName, birthDate, email,
                phone, address, affiliationType, Instant.now());
    }
    public String fullName() { return firstName + " " + lastName; }
}
