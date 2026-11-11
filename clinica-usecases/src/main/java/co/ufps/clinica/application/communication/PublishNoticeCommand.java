package co.ufps.clinica.application.communication;

import co.ufps.clinica.domain.clinic.BranchId;
import java.time.Instant;

public record PublishNoticeCommand(String title, String body, Instant expiresAt, BranchId branchId) {}
