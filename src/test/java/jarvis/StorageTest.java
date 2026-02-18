package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void saveAndLoad_roundTrip_preservesTaskData() throws Exception {
        Path dataFilePath = tempDir.resolve("Jarvis.txt");
        Storage storage = new Storage(dataFilePath);

        Todo todo = new Todo("borrow book");
        todo.setPriority(Priority.HIGH);

        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0), true);
        deadline.setPriority(Priority.MEDIUM);

        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), true,
                LocalDateTime.of(2019, 12, 2, 16, 0), true);
        event.setPriority(Priority.LOW);
        event.markAsDone();

        List<Task> tasksToSave = List.of(todo, deadline, event);
        storage.saveTasks(tasksToSave);

        List<Task> loaded = storage.loadTasks();
        assertEquals(3, loaded.size());

        assertTodo(loaded.get(0), "borrow book", false, Priority.HIGH);
        assertDeadline(loaded.get(1),
                "return book",
                false,
                LocalDateTime.of(2019, 12, 2, 18, 0),
                true,
                Priority.MEDIUM);
        assertEvent(loaded.get(2),
                "project meeting",
                true,
                LocalDateTime.of(2019, 12, 2, 14, 0),
                true,
                LocalDateTime.of(2019, 12, 2, 16, 0),
                true,
                Priority.LOW);
    }

    @Test
    public void loadTasks_corruptedFile_movesToBackup() throws Exception {
        Path dataFilePath = tempDir.resolve("Jarvis.txt");
        Storage storage = new Storage(dataFilePath);

        Files.writeString(dataFilePath, "TODO\t2\tbad\n");

        assertThrows(JarvisException.class, storage::loadTasks);

        assertFalse(Files.exists(dataFilePath));
        assertTrue(Files.exists(tempDir.resolve("Jarvis.txt.corrupted")));
    }

    @Test
    public void loadTasks_legacyFormatWithoutPriority_defaultsToNone() throws Exception {
        Path dataFilePath = tempDir.resolve("Jarvis.txt");
        Storage storage = new Storage(dataFilePath);

        Files.writeString(dataFilePath, String.join("\n",
                "TODO\t0\tborrow book",
                "DEADLINE\t0\treturn book\t2019-12-02 18:00",
                "EVENT\t1\tproject meeting\t2019-12-02 14:00\t2019-12-02 16:00",
                ""));

        List<Task> loaded = storage.loadTasks();
        assertEquals(3, loaded.size());

        assertTodo(loaded.get(0), "borrow book", false, Priority.NONE);
        assertDeadline(loaded.get(1),
                "return book",
                false,
                LocalDateTime.of(2019, 12, 2, 18, 0),
                true,
                Priority.NONE);
        assertEvent(loaded.get(2),
                "project meeting",
                true,
                LocalDateTime.of(2019, 12, 2, 14, 0),
                true,
                LocalDateTime.of(2019, 12, 2, 16, 0),
                true,
                Priority.NONE);
    }

    @Test
    public void saveAndLoad_descriptionWithEscapes_roundTripPreservesDescription() throws Exception {
        Path dataFilePath = tempDir.resolve("Jarvis.txt");
        Storage storage = new Storage(dataFilePath);

        String description = "line1\tline2\\line3\nline4";
        Todo todo = new Todo(description);

        storage.saveTasks(List.of(todo));
        List<Task> loaded = storage.loadTasks();

        assertEquals(1, loaded.size());
        assertEquals(description, loaded.get(0).getDescription());
    }

    private static Todo assertTodo(Task task, String description, boolean isDone, Priority priority) {
        assertTrue(task instanceof Todo);
        assertEquals(description, task.getDescription());
        assertEquals(isDone, task.isDone());
        assertEquals(priority, task.getPriority());
        return (Todo) task;
    }

    private static Deadline assertDeadline(
            Task task,
            String description,
            boolean isDone,
            LocalDateTime by,
            boolean hasTime,
            Priority priority) {
        assertTrue(task instanceof Deadline);
        Deadline deadline = (Deadline) task;
        assertEquals(description, deadline.getDescription());
        assertEquals(isDone, deadline.isDone());
        assertEquals(by, deadline.getBy());
        assertEquals(hasTime, deadline.hasTime());
        assertEquals(priority, deadline.getPriority());
        return deadline;
    }

    private static Event assertEvent(
            Task task,
            String description,
            boolean isDone,
            LocalDateTime from,
            boolean hasTimeFrom,
            LocalDateTime to,
            boolean hasTimeTo,
            Priority priority) {
        assertTrue(task instanceof Event);
        Event event = (Event) task;
        assertEquals(description, event.getDescription());
        assertEquals(isDone, event.isDone());
        assertEquals(from, event.getFrom());
        assertEquals(hasTimeFrom, event.hasTimeFrom());
        assertEquals(to, event.getTo());
        assertEquals(hasTimeTo, event.hasTimeTo());
        assertEquals(priority, event.getPriority());
        return event;
    }
}
