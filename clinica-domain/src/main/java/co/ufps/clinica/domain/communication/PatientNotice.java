package co.ufps.clinica.domain.communication;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.Instant;
public record PatientNotice(PatientNoticeId id, String title, String body, Instant publishedAt, Instant expiresAt,
                            BranchId branchId) {}
