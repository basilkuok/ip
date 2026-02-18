package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

    @Test
    public void findByKeyword_caseInsensitive_returnsMatchesInOrder() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Todo("Borrow book"),
                new Todo("return BOOK"),
                new Todo("buy milk")));

        List<Task> matches = tasks.findByKeyword("book");
        assertEquals(2, matches.size());
        assertTrue(matches.get(0) instanceof Todo);
        assertEquals("Borrow book", matches.get(0).getDescription());
        assertEquals("return BOOK", matches.get(1).getDescription());
    }

    @Test
    public void findByKeyword_blank_throwsUsage() {
        TaskList tasks = new TaskList();

        JarvisException exception = assertThrows(
                JarvisException.class,
                () -> tasks.findByKeyword("   "));
        assertEquals("Please use: find <keyword>", exception.getMessage());
    }

    @Test
    public void getRaw_zeroBasedIndex_returnsTaskWithoutValidation() throws Exception {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("borrow book");
        tasks.add(todo);

        assertEquals(todo, tasks.getRaw(0));
    }
}

