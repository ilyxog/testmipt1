package ru.mipt.timeapi;

import org.junit.jupiter.api.Test;
import ru.mipt.timeapi.scheduler.MaintenanceScheduler;
import ru.mipt.timeapi.scheduler.MaintenanceWindow;
import ru.mipt.timeapi.scheduler.OverlapPolicy;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaintenanceSchedulerTest {
    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Test
    void rejectsLocalTimeInsideSpringDstGap() {
        MaintenanceScheduler scheduler = new MaintenanceScheduler(Clock.systemUTC());

        assertThrows(DateTimeException.class, () -> scheduler.nextWindow(
                LocalDate.of(2024, 3, 31),
                LocalTime.of(2, 30),
                Duration.ofMinutes(30),
                BERLIN,
                OverlapPolicy.EARLIER));
    }

    @Test
    void selectsEarlierAndLaterOffsetsDuringAutumnOverlap() {
        MaintenanceScheduler scheduler = new MaintenanceScheduler(Clock.systemUTC());
        LocalDate date = LocalDate.of(2024, 10, 27);
        LocalTime time = LocalTime.of(2, 30);

        MaintenanceWindow earlier = scheduler.nextWindow(
                date, time, Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);
        MaintenanceWindow later = scheduler.nextWindow(
                date, time, Duration.ofMinutes(30), BERLIN, OverlapPolicy.LATER);

        assertEquals(ZoneOffset.ofHours(2), earlier.start().atZone(BERLIN).getOffset());
        assertEquals(ZoneOffset.ofHours(1), later.start().atZone(BERLIN).getOffset());
        assertEquals(Duration.ofHours(1), Duration.between(earlier.start(), later.start()));
    }

    @Test
    void dailyWindowsKeepLocalTimeAcrossSpringDstAndAreDeterministic() {
        Clock clock = Clock.fixed(
                Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(clock);

        List<MaintenanceWindow> firstCall = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);
        List<MaintenanceWindow> secondCall = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);

        assertEquals(firstCall, secondCall);
        assertEquals(LocalTime.of(3, 30), firstCall.get(0).start().atZone(BERLIN).toLocalTime());
        assertEquals(LocalTime.of(3, 30), firstCall.get(1).start().atZone(BERLIN).toLocalTime());
        assertEquals(Duration.ofHours(23),
                Duration.between(firstCall.get(0).start(), firstCall.get(1).start()));
    }

    @Test
    void handlesZeroAndNegativeDayCountsAndNonPositiveDuration() {
        MaintenanceScheduler scheduler = new MaintenanceScheduler(Clock.systemUTC());
        assertEquals(List.of(), scheduler.upcomingWindows(
                0, LocalTime.NOON, Duration.ofMinutes(5), BERLIN, OverlapPolicy.EARLIER));
        assertThrows(IllegalArgumentException.class, () -> scheduler.upcomingWindows(
                -1, LocalTime.NOON, Duration.ofMinutes(5), BERLIN, OverlapPolicy.EARLIER));
        assertThrows(IllegalArgumentException.class, () -> scheduler.nextWindow(
                LocalDate.now(), LocalTime.NOON, Duration.ZERO, BERLIN, OverlapPolicy.EARLIER));
    }
}
