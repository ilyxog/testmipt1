package ru.mipt.timeapi;

import org.junit.jupiter.api.Test;
import ru.mipt.timeapi.model.Incident;
import ru.mipt.timeapi.parser.IncidentParser;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentParserTest {
    @Test
    void parsesExampleAndCalculatesDuration() {
        Incident incident = IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");

        assertEquals("INC-1", incident.id());
        assertEquals("billing", incident.service());
        assertEquals(Duration.ofMinutes(150), incident.duration());
    }

    @Test
    void rejectsWrongNumberOfFieldsAndEmptyRequiredValues() {
        assertThrows(IllegalArgumentException.class,
                () -> IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z"));
        assertThrows(IllegalArgumentException.class,
                () -> IncidentParser.parse(";billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class,
                () -> IncidentParser.parse("INC-1;;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
    }

    @Test
    void rejectsInvalidTimestampAndResolvedTimeBeforeStart() {
        assertThrows(IllegalArgumentException.class,
                () -> IncidentParser.parse("INC-1;billing;not-an-instant;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class,
                () -> IncidentParser.parse("INC-1;billing;2024-03-31T01:00:00Z;2024-03-30T22:30:00Z"));
    }

    @Test
    void incidentRejectsNullValues() {
        assertThrows(NullPointerException.class,
                () -> new Incident("id", "service", Instant.EPOCH, null));
    }
}
