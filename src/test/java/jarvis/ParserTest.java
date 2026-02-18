package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParserTest {
    private static final String EVENT_USAGE_MESSAGE =
            "Please use: event <description> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd> [HHmm]";
    private static final String DEADLINE_USAGE_MESSAGE =
            "Please use: deadline <description> /by <yyyy-mm-dd> [HHmm]";
    private static final String PRIORITY_USAGE_MESSAGE =
            "Please use: priority <taskNumber> <low|medium|high|none>";

    private final Parser parser = new Parser();

    @Test
    public void parseSingleIndexCommand_valid_parsesTaskNumber() throws Exception {
        assertEquals(2, parser.parseSingleIndexCommand("mark 2", "mark"));
    }

    @Test
    public void parseSingleIndexCommand_wrongKeyword_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseSingleIndexCommand("unmark 2", "mark"));
        assertEquals("Please use: mark <taskNumber>", exception.getMessage());
    }

    @Test
    public void parseSingleIndexCommand_extraArguments_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseSingleIndexCommand("mark 2 extra", "mark"));
        assertEquals("Please use: mark <taskNumber>", exception.getMessage());
    }

    @Test
    public void parseSingleIndexCommand_negativeIndex_throwsValidTaskNumberMessage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseSingleIndexCommand("delete -1", "delete"));
        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    public void parseDeadline_repeatedBy_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseDeadline("deadline return book /by 2019-12-02 /by 2019-12-03"));
        assertEquals(DEADLINE_USAGE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDeadline_extraSpaces_parsesCorrectly() throws Exception {
        Deadline deadline = parser.parseDeadline("deadline return book   /by   2019-12-02");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertEquals(false, deadline.hasTime());
    }

    @Test
    public void parseEvent_repeatedTo_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseEvent("event meeting /from 2019-12-02 /to 2019-12-03 /to 2019-12-04"));
        assertEquals(EVENT_USAGE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseEvent_startNotBeforeEnd_throwsError() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parseEvent("event meeting /from 2019-12-02 1400 /to 2019-12-02 1400"));
        assertEquals("Event start must be earlier than end.\n" + EVENT_USAGE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parsePriorityUpdate_valid_parsesCorrectly() throws Exception {
        Parser.PriorityUpdate update = parser.parsePriorityUpdate("priority 3 high");
        assertEquals(3, update.taskNumber());
        assertEquals(Priority.HIGH, update.priority());
    }

    @Test
    public void parsePriorityUpdate_extraArguments_throwsUsage() {
        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> parser.parsePriorityUpdate("priority 3 high extra"));
        assertEquals(PRIORITY_USAGE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseFindKeyword_multipleSpaces_trimsToKeyword() throws Exception {
        assertEquals("book", parser.parseFindKeyword("find    book   "));
    }

    @Test
    public void parseTodo_multipleSpaces_trimsToDescription() throws Exception {
        Todo todo = parser.parseTodo("todo    borrow book   ");
        assertEquals("borrow book", todo.getDescription());
    }
}
