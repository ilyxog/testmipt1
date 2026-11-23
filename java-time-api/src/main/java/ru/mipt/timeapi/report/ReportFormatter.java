package ru.mipt.timeapi.report;

import ru.mipt.timeapi.scheduler.MaintenanceWindow;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class ReportFormatter {
    private static final String PATTERN = "dd.MM.uuuu HH:mm XXX (VV)";

    private ReportFormatter() {
    }

    public static String formatWindow(
            MaintenanceWindow window, ZoneId zone, Locale locale) {
        Objects.requireNonNull(window, "window must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(locale, "locale must not be null");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN, locale);
        String start = formatter.format(window.start().atZone(zone));
        String end = formatter.format(window.end().atZone(zone));
        return start + " – " + end;
    }
}
