package co.ufps.clinica.domain.appointment;
public enum AppointmentStatus {
    REQUESTED, CONFIRMED, CHECKED_IN, IN_CONSULTATION, COMPLETED, CANCELLED, NO_SHOW;
    public boolean canTransitionTo(AppointmentStatus next) {
        return switch (this) {
            case REQUESTED -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == CHECKED_IN || next == CANCELLED || next == NO_SHOW;
            case CHECKED_IN -> next == IN_CONSULTATION || next == CANCELLED;
            case IN_CONSULTATION -> next == COMPLETED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };
    }
}
