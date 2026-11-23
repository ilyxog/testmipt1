package ru.mipt.timeapi.legacy;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public final class LegacyDateAdapter {
    private LegacyDateAdapter() {
    }

    public static Instant toInstant(Date date) {
        return Objects.requireNonNull(date, "date must not be null").toInstant();
    }

    public static Date toDate(Instant instant) {
        return Date.from(Objects.requireNonNull(instant, "instant must not be null"));
    }

    public static ZonedDateTime toZonedDateTime(GregorianCalendar calendar) {
        return Objects.requireNonNull(calendar, "calendar must not be null").toZonedDateTime();
    }

    public static GregorianCalendar toGregorianCalendar(ZonedDateTime dateTime) {
        return GregorianCalendar.from(
                Objects.requireNonNull(dateTime, "dateTime must not be null"));
    }
}
