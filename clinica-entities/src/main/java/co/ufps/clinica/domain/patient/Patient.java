package co.ufps.clinica.domain.patient;

import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
import java.time.LocalDate;

public record Patient(PatientId id, String documentType, String documentNumber, String firstName, String lastName,
                      LocalDate birthDate, String email, String phone, String address, String affiliationType,
                      Instant createdAt, UserId accountUserId) {
    public static Patient register(String documentType, String documentNumber, String firstName, String lastName,
                                   LocalDate birthDate, String email, String phone, String address, String affiliationType) {
        return new Patient(PatientId.generate(), documentType, documentNumber, firstName, lastName, birthDate, email,
                phone, address, affiliationType, Instant.now(), null);
    }
    public String fullName() { return firstName + " " + lastName; }
    public Patient linkedToAccount(UserId userId) {
        if (userId == null) throw new IllegalArgumentException("El usuario asociado es obligatorio");
        if (accountUserId != null && !accountUserId.equals(userId)) throw new IllegalStateException("El paciente ya tiene una cuenta asociada");
        return new Patient(id, documentType, documentNumber, firstName, lastName, birthDate, email, phone, address,
                affiliationType, createdAt, userId);
    }
}
