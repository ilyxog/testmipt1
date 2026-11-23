package ru.mipt.timeapi;

import org.junit.jupiter.api.Test;
import ru.mipt.timeapi.analytics.IncidentAnalytics;
import ru.mipt.timeapi.model.Incident;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentAnalyticsTest {
    @Test
    void calculatesTotalAverageAndLongestIncidentWithTieBreakers() {
        Instant earlier = Instant.parse("2024-01-01T00:00:00Z");
        Incident latestStart = new Incident("A", "svc", earlier.plusSeconds(10), earlier.plusSeconds(15));
        Incident tieLargerId = new Incident("Z", "svc", earlier, earlier.plusSeconds(5));
        Incident tieSmallerId = new Incident("B", "svc", earlier, earlier.plusSeconds(5));
        Incident shorter = new Incident("C", "svc", earlier, earlier.plusSeconds(4));
        List<Incident> incidents = List.of(latestStart, tieLargerId, tieSmallerId, shorter);

        assertEquals(Duration.ofSeconds(19), IncidentAnalytics.totalDowntime(incidents));
        assertEquals(Duration.ofMillis(4_750), IncidentAnalytics.averageDowntime(incidents));
        assertEquals(Optional.of(tieSmallerId), IncidentAnalytics.longestIncident(incidents));
    }

    @Test
    void averageRoundsDownToWholeNanoseconds() {
        Instant start = Instant.EPOCH;
        List<Incident> incidents = List.of(
                new Incident("one", "svc", start, start.plusNanos(1)),
                new Incident("two", "svc", start, start.plusNanos(2)));

        assertEquals(Duration.ofNanos(3), IncidentAnalytics.totalDowntime(incidents));
        assertEquals(Duration.ofNanos(1), IncidentAnalytics.averageDowntime(incidents));
    }

    @Test
    void emptyListReturnsZeroAndNoLongestIncident() {
        assertEquals(Duration.ZERO, IncidentAnalytics.totalDowntime(List.of()));
        assertEquals(Duration.ZERO, IncidentAnalytics.averageDowntime(List.of()));
        assertEquals(Optional.empty(), IncidentAnalytics.longestIncident(List.of()));
    }

    @Test
    void groupsByLocalDateAndSortsDatesAndIncidents() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        Incident firstInLocalDay = new Incident("B", "svc",
                Instant.parse("2024-03-30T23:30:00Z"),
                Instant.parse("2024-03-30T23:31:00Z"));
        Incident secondInLocalDay = new Incident("A", "svc",
                Instant.parse("2024-03-30T23:45:00Z"),
                Instant.parse("2024-03-30T23:46:00Z"));
        Incident previousLocalDay = new Incident("C", "svc",
                Instant.parse("2024-03-30T10:00:00Z"),
                Instant.parse("2024-03-30T10:01:00Z"));
        List<Incident> input = List.of(secondInLocalDay, previousLocalDay, firstInLocalDay);

        Map<LocalDate, List<Incident>> grouped = IncidentAnalytics.groupByStartDate(input, berlin);

        assertEquals(List.of(LocalDate.of(2024, 3, 30), LocalDate.of(2024, 3, 31)),
                List.copyOf(grouped.keySet()));
        assertEquals(List.of(firstInLocalDay, secondInLocalDay),
                grouped.get(LocalDate.of(2024, 3, 31)));
        assertEquals(LocalDate.of(2024, 3, 31),
                firstInLocalDay.startedAt().atZone(berlin).toLocalDate());
        assertEquals(List.of(secondInLocalDay, previousLocalDay, firstInLocalDay), input);
    }

    @Test
    void rejectsNullListAndNullZone() {
        assertThrows(NullPointerException.class,
                () -> IncidentAnalytics.totalDowntime(null));
        assertThrows(NullPointerException.class,
                () -> IncidentAnalytics.groupByStartDate(List.of(), null));
    }
}
