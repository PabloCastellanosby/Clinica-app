package co.ufps.clinica.adapters.patient;

import java.time.LocalDate;

public record UpdatePatientRequest(String documentType, String documentNumber, String firstName, String lastName,
                                   LocalDate birthDate, String email, String phone, String address,
                                   String affiliationType) {}
