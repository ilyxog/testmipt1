package ru.mipt.timeapi;

import org.junit.jupiter.api.Test;
import ru.mipt.timeapi.legacy.LegacyDateAdapter;
import ru.mipt.timeapi.report.ReportFormatter;
import ru.mipt.timeapi.scheduler.MaintenanceWindow;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportAndLegacyAdapterTest {
    @Test
    void formatsWindowExactlyAsRequired() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        MaintenanceWindow window = new MaintenanceWindow(
                Instant.parse("2024-03-31T01:00:00Z"),
                Instant.parse("2024-03-31T02:00:00Z"));

        assertEquals(
                "31.03.2024 03:00 +02:00 (Europe/Berlin) – "
                        + "31.03.2024 04:00 +02:00 (Europe/Berlin)",
                ReportFormatter.formatWindow(window, berlin, Locale.US));
    }

    @Test
    void dateAndInstantRoundTripPreservesMomentToMillisecondPrecision() {
        Instant original = Instant.parse("2024-03-31T01:02:03.456Z");
        Date date = LegacyDateAdapter.toDate(original);

        assertEquals(original, LegacyDateAdapter.toInstant(date));
    }

    @Test
    void calendarConversionsPreserveInstantAndZone() {
        ZoneId zone = ZoneId.of("Europe/Berlin");
        Instant instant = Instant.parse("2024-10-27T01:30:00Z");
        GregorianCalendar originalCalendar = new GregorianCalendar(TimeZone.getTimeZone(zone));
        originalCalendar.setTimeInMillis(instant.toEpochMilli());

        var zonedDateTime = LegacyDateAdapter.toZonedDateTime(originalCalendar);
        GregorianCalendar convertedCalendar = LegacyDateAdapter.toGregorianCalendar(zonedDateTime);

        assertEquals(instant, zonedDateTime.toInstant());
        assertEquals(zone, zonedDateTime.getZone());
        assertEquals(instant.toEpochMilli(), convertedCalendar.getTimeInMillis());
        assertEquals(zone, convertedCalendar.getTimeZone().toZoneId());
    }
}
