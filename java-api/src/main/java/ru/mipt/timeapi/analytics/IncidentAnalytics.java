package ru.mipt.timeapi.analytics;

import ru.mipt.timeapi.model.Incident;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

public final class IncidentAnalytics {
    private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);

    private static final Comparator<Incident> BY_START_THEN_ID =
            Comparator.comparing(Incident::startedAt)
                    .thenComparing(Incident::id);

    private IncidentAnalytics() {
    }

    public static Duration totalDowntime(List<Incident> incidents) {
        checkIncidents(incidents);

        Duration total = Duration.ZERO;
        for (Incident incident : incidents) {
            total = total.plus(incident.duration());
        }
        return total;
    }

    public static Duration averageDowntime(List<Incident> incidents) {
        checkIncidents(incidents);
        if (incidents.isEmpty()) {
            return Duration.ZERO;
        }

        BigInteger totalNanos = BigInteger.ZERO;
        for (Incident incident : incidents) {
            Duration duration = incident.duration();
            BigInteger incidentNanos = BigInteger.valueOf(duration.getSeconds())
                    .multiply(NANOS_PER_SECOND)
                    .add(BigInteger.valueOf(duration.getNano()));
            totalNanos = totalNanos.add(incidentNanos);
        }

        BigInteger averageNanos = totalNanos.divide(BigInteger.valueOf(incidents.size()));
        BigInteger[] secondsAndNanos = averageNanos.divideAndRemainder(NANOS_PER_SECOND);
        return Duration.ofSeconds(
                secondsAndNanos[0].longValueExact(),
                secondsAndNanos[1].longValueExact());
    }

    public static Optional<Incident> longestIncident(List<Incident> incidents) {
        checkIncidents(incidents);

        Comparator<Incident> selectionOrder =
                Comparator.comparing(Incident::duration).reversed()
                        .thenComparing(Incident::startedAt)
                        .thenComparing(Incident::id);

        return incidents.stream().min(selectionOrder);
    }

    public static Map<LocalDate, List<Incident>> groupByStartDate(
            List<Incident> incidents, ZoneId zone) {
        checkIncidents(incidents);
        Objects.requireNonNull(zone, "zone must not be null");

        Map<LocalDate, List<Incident>> result = new TreeMap<>();
        for (Incident incident : incidents) {
            LocalDate localDate = incident.startedAt().atZone(zone).toLocalDate();
            result.computeIfAbsent(localDate, ignored -> new ArrayList<>()).add(incident);
        }

        for (List<Incident> group : result.values()) {
            group.sort(BY_START_THEN_ID);
        }
        return result;
    }

    private static void checkIncidents(List<Incident> incidents) {
        Objects.requireNonNull(incidents, "incidents must not be null");
        for (Incident incident : incidents) {
            Objects.requireNonNull(incident, "incidents must not contain null elements");
        }
    }
}
