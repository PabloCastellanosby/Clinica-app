package co.ufps.clinica.application.reports;
public record AttendanceMetrics(long scheduled, long cancelled, long noShows) {
    public double cancellationRate() { return scheduled == 0 ? 0 : (double) cancelled / scheduled; }
    public double noShowRate() { return scheduled == 0 ? 0 : (double) noShows / scheduled; }
}
