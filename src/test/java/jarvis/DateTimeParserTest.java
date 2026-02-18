package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateTimeParserTest {
    @Test
    public void parseUserDateTime_dateOnly_parsesAsStartOfDay() throws Exception {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseUserDateTime(
                "2019-12-02",
                "unused");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), parsed.getValue());
        assertFalse(parsed.hasTime());
        assertEquals("Dec 2 2019", DateTimeParser.formatForDisplay(parsed.getValue(), parsed.hasTime()));
    }

    @Test
    public void parseUserDateTime_dateAndTime_parsesCorrectly() throws Exception {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseUserDateTime(
                "2/12/2019 1800",
                "unused");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.getValue());
        assertTrue(parsed.hasTime());
        assertEquals("Dec 2 2019, 18:00", DateTimeParser.formatForDisplay(parsed.getValue(), parsed.hasTime()));
    }

    @Test
    public void parseUserDateTime_invalid_throwsWithUsageMessage() {
        String usageMessage = "Please use: deadline <description> /by <yyyy-mm-dd> [HHmm]";
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> DateTimeParser.parseUserDateTime("Sunday", usageMessage));
        assertEquals(usageMessage, exception.getMessage());
    }

    @Test
    public void parseUserDateTime_invalidDate_throwsWithUsageMessage() {
        String usageMessage = "Please use: deadline <description> /by <yyyy-mm-dd> [HHmm]";
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> DateTimeParser.parseUserDateTime("2019-02-30", usageMessage));
        assertEquals(usageMessage, exception.getMessage());
    }

    @Test
    public void parseStoredDateTime_dateTimeWithColon_parsesCorrectly() throws Exception {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseStoredDateTime("2019-12-02 18:00", 2);

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.getValue());
        assertTrue(parsed.hasTime());
    }

    @Test
    public void parseStoredDateTime_empty_throwsCorruptionMessage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> DateTimeParser.parseStoredDateTime("   ", 5));
        assertEquals("Data file corrupted at line 5: empty date/time.", exception.getMessage());
    }

    @Test
    public void parseStoredDateTime_invalid_throwsCorruptionMessage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> DateTimeParser.parseStoredDateTime("tomorrow", 7));
        assertEquals("Data file corrupted at line 7: invalid date/time.", exception.getMessage());
    }
}


