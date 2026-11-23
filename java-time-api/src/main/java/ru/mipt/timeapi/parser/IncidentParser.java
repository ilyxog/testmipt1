package ru.mipt.timeapi.parser;

import ru.mipt.timeapi.model.Incident;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public final class IncidentParser {
    private IncidentParser() {
    }

    public static Incident parse(String line) {
        Objects.requireNonNull(line, "line must not be null");

        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException(
                    "Incident line must contain exactly 4 fields separated by ';'");
        }

        String id = fields[0].trim();
        String service = fields[1].trim();
        String startedAtText = fields[2].trim();
        String resolvedAtText = fields[3].trim();

        if (id.isEmpty() || service.isEmpty()
                || startedAtText.isEmpty() || resolvedAtText.isEmpty()) {
            throw new IllegalArgumentException("Incident fields must not be empty");
        }

        try {
            Instant startedAt = Instant.parse(startedAtText);
            Instant resolvedAt = Instant.parse(resolvedAtText);
            return new Incident(id, service, startedAt, resolvedAt);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Timestamps must be valid ISO-8601 Instant values", exception);
        }
    }
}
