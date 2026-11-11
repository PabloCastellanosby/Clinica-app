package co.ufps.clinica.application.port.out.clinic;

import co.ufps.clinica.domain.clinic.*;
import java.util.List;
import java.util.Optional;

public interface ClinicRepository {
    Clinic saveClinic(Clinic clinic);
    Optional<Clinic> findById(ClinicId id);
    List<Clinic> findAllActive();
    Branch saveBranch(Branch branch);
    Optional<Branch> findBranchById(BranchId id);
    List<Branch> findBranchesByClinic(ClinicId clinicId);
    Office saveOffice(Office office);
    Optional<Office> findOfficeById(OfficeId id);
    List<Office> findOfficesByBranch(BranchId branchId);
}
