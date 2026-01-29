package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void add_whenFull_throwsException() throws Exception {
        TaskList tasks = new TaskList();
        for (int taskNumber = 1; taskNumber <= 100; taskNumber++) {
            tasks.add(new Todo("task " + taskNumber));
        }

        JarvisException exception = assertThrows(JarvisException.class, () -> tasks.add(new Todo("extra task")));
        assertEquals("Sorry, I can only store 100 tasks.", exception.getMessage());
    }

    @Test
    public void get_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        JarvisException exception = assertThrows(JarvisException.class, () -> tasks.get(1));
        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    public void remove_validTaskNumber_removesTask() throws Exception {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(1);
        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(1));
    }
}

