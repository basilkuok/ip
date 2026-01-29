package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0), true);
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), true,
                LocalDateTime.of(2019, 12, 2, 16, 0), true);

        event.markAsDone();

        List<Task> tasksToSave = List.of(todo, deadline, event);
        storage.saveTasks(tasksToSave);

        List<Task> loaded = storage.loadTasks();
        assertEquals(3, loaded.size());

        assertTrue(loaded.get(0) instanceof Todo);
        assertEquals("borrow book", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());

        assertTrue(loaded.get(1) instanceof Deadline);
        Deadline loadedDeadline = (Deadline) loaded.get(1);
        assertEquals("return book", loadedDeadline.getDescription());
        assertFalse(loadedDeadline.isDone());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), loadedDeadline.getBy());
        assertTrue(loadedDeadline.hasTime());

        assertTrue(loaded.get(2) instanceof Event);
        Event loadedEvent = (Event) loaded.get(2);
        assertEquals("project meeting", loadedEvent.getDescription());
        assertTrue(loadedEvent.isDone());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), loadedEvent.getFrom());
        assertTrue(loadedEvent.hasTimeFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), loadedEvent.getTo());
        assertTrue(loadedEvent.hasTimeTo());
    }

    @Test
    public void loadTasks_corruptedFile_movesToBackup() throws Exception {
        Path dataFilePath = tempDir.resolve("Jarvis.txt");
        Storage storage = new Storage(dataFilePath);

        Files.writeString(dataFilePath, "TODO\t2\tbad\n");

        try {
            storage.loadTasks();
        } catch (JarvisException exception) {
            // expected
        }

        assertFalse(Files.exists(dataFilePath));
        assertTrue(Files.exists(tempDir.resolve("Jarvis.txt.corrupted")));
    }
}

