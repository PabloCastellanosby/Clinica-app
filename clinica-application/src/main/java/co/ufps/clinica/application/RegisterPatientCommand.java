package co.ufps.clinica.application;

import java.time.LocalDate;

public record RegisterPatientCommand(String documentType, String documentNumber, String firstName, String lastName,
                                     LocalDate birthDate, String email, String phone, String address,
                                     String affiliationType) {}
