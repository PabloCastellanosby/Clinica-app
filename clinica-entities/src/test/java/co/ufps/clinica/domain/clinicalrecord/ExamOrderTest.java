package co.ufps.clinica.domain.clinicalrecord;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExamOrderTest {
    @Test
    void completesOrderWithResultAndRejectsSecondCompletion() {
        Instant orderedAt = Instant.parse("2026-11-11T12:00:00Z");
        ExamOrder order = new ExamOrder(ExamOrderId.generate(), EncounterId.generate(), "Hemograma", null,
                ExamOrderStatus.ORDERED, orderedAt, null, null);
        ExamOrder completed = order.complete("Resultado disponible", orderedAt.plusSeconds(60));

        assertEquals(ExamOrderStatus.COMPLETED, completed.status());
        assertEquals("Resultado disponible", completed.result());
        assertThrows(IllegalStateException.class,
                () -> completed.complete("Reemplazo", orderedAt.plusSeconds(120)));
    }

    @Test
    void requiresResultForCompletion() {
        Instant orderedAt = Instant.parse("2026-11-11T12:00:00Z");
        ExamOrder order = new ExamOrder(ExamOrderId.generate(), EncounterId.generate(), "Hemograma", null,
                ExamOrderStatus.ORDERED, orderedAt, null, null);

        assertThrows(IllegalArgumentException.class, () -> order.complete(" ", orderedAt.plusSeconds(60)));
    }
}
