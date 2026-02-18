package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PriorityTest {
    @Test
    public void parseUserInput_mixedCase_parsesCorrectly() throws Exception {
        assertEquals(Priority.HIGH, Priority.parseUserInput(" High "));
        assertEquals(Priority.MEDIUM, Priority.parseUserInput("MEDIUM"));
        assertEquals(Priority.LOW, Priority.parseUserInput("low"));
        assertEquals(Priority.NONE, Priority.parseUserInput("none"));
    }

    @Test
    public void parseUserInput_invalid_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> Priority.parseUserInput("urgent"));
        assertEquals("Please use: priority <taskNumber> <low|medium|high|none>", exception.getMessage());
    }

    @Test
    public void parseStoredValue_blank_returnsNone() throws Exception {
        assertEquals(Priority.NONE, Priority.parseStoredValue("", 3));
        assertEquals(Priority.NONE, Priority.parseStoredValue("   ", 3));
    }

    @Test
    public void parseStoredValue_invalid_throwsCorruptionMessage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> Priority.parseStoredValue("urgent", 7));
        assertEquals("Data file corrupted at line 7: invalid priority.", exception.getMessage());
    }
}

