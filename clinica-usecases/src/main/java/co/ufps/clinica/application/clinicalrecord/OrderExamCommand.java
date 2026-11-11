package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.EncounterId;

public record OrderExamCommand(EncounterId encounterId, String name, String instructions) {}
