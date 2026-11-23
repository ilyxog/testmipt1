package ru.mipt.timeapi.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record MaintenanceWindow(Instant start, Instant end) {
    public MaintenanceWindow {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Maintenance window duration must be positive");
        }
    }

    public Duration duration() {
        return Duration.between(start, end);
    }
}
