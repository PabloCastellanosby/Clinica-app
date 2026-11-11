package co.ufps.clinica.frameworks.persistence.inventory;

import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.inventory.*;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.application.port.out.inventory.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryRepositoryJpa implements InventoryRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Medicine save(Medicine medicine) {
        entityManager.merge(toJpa(medicine));
        return medicine;
    }

    @Override public Optional<Medicine> findById(MedicineId id) {
        MedicineJpa jpa = entityManager.find(MedicineJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }

    @Override public List<Medicine> findAllActive() {
        return entityManager.createQuery("SELECT m FROM MedicineJpa m WHERE m.active = true", MedicineJpa.class)
                .getResultList().stream().map(this::toDomain).toList();
    }

    @Override @Transactional public StockLot saveLot(StockLot lot) {
        entityManager.merge(toJpa(lot));
        return lot;
    }

    @Override public Optional<StockLot> findLotById(StockLotId id) {
        StockLotJpa jpa = entityManager.find(StockLotJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainLot);
    }

    @Override public List<StockLot> findLotsByMedicine(MedicineId medicineId) {
        return entityManager.createQuery("SELECT l FROM StockLotJpa l WHERE l.medicineId = :mid", StockLotJpa.class)
                .setParameter("mid", medicineId.value()).getResultList().stream().map(this::toDomainLot).toList();
    }

    @Override @Transactional public StockMovement saveMovement(StockMovement movement) {
        entityManager.merge(toJpa(movement));
        return movement;
    }

    private MedicineJpa toJpa(Medicine m) {
        MedicineJpa jpa = new MedicineJpa();
        jpa.setId(m.id().value()); jpa.setName(m.name());
        jpa.setPresentation(m.presentation()); jpa.setDosageForm(m.dosageForm());
        jpa.setUnit(m.unit()); jpa.setActive(m.active());
        return jpa;
    }
    private Medicine toDomain(MedicineJpa jpa) {
        return new Medicine(MedicineId.of(jpa.getId()), jpa.getName(), jpa.getPresentation(),
                jpa.getDosageForm(), jpa.getUnit(), jpa.isActive());
    }

    private StockLotJpa toJpa(StockLot l) {
        StockLotJpa jpa = new StockLotJpa();
        jpa.setId(l.id().value()); jpa.setBranchId(l.branchId().value());
        jpa.setMedicineId(l.medicineId().value()); jpa.setLotNumber(l.lotNumber());
        jpa.setQuantity(l.quantity()); jpa.setExpiresAt(l.expiresAt()); jpa.setUnitCost(l.unitCost());
        return jpa;
    }
    private StockLot toDomainLot(StockLotJpa jpa) {
        return new StockLot(StockLotId.of(jpa.getId()), BranchId.of(jpa.getBranchId()),
                MedicineId.of(jpa.getMedicineId()), jpa.getLotNumber(),
                jpa.getQuantity(), jpa.getExpiresAt(), jpa.getUnitCost());
    }

    private StockMovementJpa toJpa(StockMovement m) {
        StockMovementJpa jpa = new StockMovementJpa();
        jpa.setId(m.id().value()); jpa.setLotId(m.lotId().value());
        jpa.setType(m.type().name()); jpa.setQuantity(m.quantity());
        jpa.setReason(m.reason()); jpa.setActorId(m.actorId().value());
        jpa.setOccurredAt(m.occurredAt());
        return jpa;
    }
}
