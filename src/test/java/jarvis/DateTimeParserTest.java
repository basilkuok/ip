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
}

